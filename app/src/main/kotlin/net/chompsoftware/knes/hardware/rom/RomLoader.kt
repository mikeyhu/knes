package net.chompsoftware.knes.hardware.rom

import net.chompsoftware.knes.readFileToByteArray
import java.io.File

const val HEADER_SIZE = 16


object RomInspector {
    fun inspectRom(rom: UByteArray): RomInformation {
        return retrieveHeader(rom)
    }

    private fun retrieveHeader(rom: UByteArray): RomInformation {
        if (rom[0].toUInt() != 0x4eu ||
            rom[1].toUInt() != 0x45u ||
            rom[2].toUInt() != 0x53u ||
            rom[3].toUInt() != 0x1au
        ) {
            throw RomLoadError("Invalid header")
        }
        val isRomTypeNes2 = rom[7].and(0x0cu).toUInt() == 0x08u
        return RomInformation(
            if (isRomTypeNes2) RomType.NES_2 else RomType.NES_1,
            rom.isVerticalMirroring(),
            rom.hasBatteryBackedRam(),
            rom.mapperNumber(),
            rom.prgRomSize(),
            rom.chrRomSize(),
            rom.size
        )
    }

    private fun UByteArray.isVerticalMirroring() = this[6] and 0x1u == (0x1u).toUByte()

    private fun UByteArray.mapperNumber(): Int {
        return (this[6] and 0xf0u).toInt().shr(4) +
                (this[7] and 0x0fu).toInt().shl(4)
    }

    private fun UByteArray.prgRomSize() = this[4].toInt() * 0x4000

    private fun UByteArray.chrRomSize() = this[5].toInt() * 0x2000

    private fun UByteArray.hasBatteryBackedRam() = this[6] and 0x2u == (0x2u).toUByte()
}

object RomLoader {
    fun loadMapper(rom: UByteArray): RomMapper {
        val info = RomInspector.inspectRom(rom)
        return when (info.mapper) {
            0 -> {
                TypeZeroRomMapper(info, rom)
            }
            else ->
                throw RomLoadError("Unsupported mapper type")
        }
    }
}

interface RomMapper {
    fun getPrgRom(position: Int): UByte
    fun getChrRom(position: Int): UByte
    fun getChrRomSlice(position: Int, size: Int): UByteArray

    fun getBatteryBackedRam(position: Int): UByte
    fun setBatteryBackedRam(position: Int, value: UByte)
}

class TypeZeroRomMapper(
    private val info: RomInformation,
    private val rom: UByteArray
) : RomMapper {

    private val batteryBackedRam = UByteArray(0x2000)

    override fun getPrgRom(position: Int): UByte {
        return when (position) {
            in 0x8000 until 0xC000 -> rom[position - 0x8000 + HEADER_SIZE]
            in 0xC000 until 0x10000 ->
                if (info.prgRom > 0x4000) rom[position - 0x8000 + HEADER_SIZE]
                else rom[position - 0xC000 + HEADER_SIZE]
            else -> throw RomMapperError("TypeZeroRomMapper: (Read) Out of Range at $position")
        }
    }

    override fun getChrRom(position: Int): UByte {
        return rom[position + info.prgRom + HEADER_SIZE]
    }

    override fun getChrRomSlice(position: Int, size: Int): UByteArray {
        return rom.copyOfRange(position + info.prgRom + HEADER_SIZE, position + info.prgRom + HEADER_SIZE + size)
    }

    override fun getBatteryBackedRam(position: Int): UByte {
        return batteryBackedRam[position - 0x6000]
    }

    override fun setBatteryBackedRam(position: Int, value: UByte) {
        batteryBackedRam[position - 0x6000] = value
    }

}

data class RomInformation(
    val romType: RomType,
    val verticalMirroring: Boolean,
    val hasBatteryBackedRam: Boolean,
    val mapper: Int,
    val prgRom: Int,
    val chrRom: Int,
    val romSize: Int
)

enum class RomType {
    NES_1,
    NES_2
}

class RomLoadError(message: String) : Error(message)

class RomMapperError(message: String) : Error(message)

// Alternate `main` that allows a ROM to be inspected
fun main(args: Array<String>) {
    val inspector = RomInspector
    val fileData = readFileToByteArray(File(args[0]))

    println(inspector.inspectRom(fileData))
}