package net.chompsoftware.knes.hardware.rom

import net.chompsoftware.knes.hardware.BasicMemory


@ExperimentalUnsignedTypes
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
}

@ExperimentalUnsignedTypes
object RomLoader {

    private const val headerSize = 16

    fun loadMemory(info: RomInformation, rom: UByteArray): BasicMemory {
        when (info.mapper) {
            0 -> {
                val array = UByteArray(0x10000)
                rom.copyInto(array, 0x8000, 0 + headerSize, 0x4000 + headerSize)
                rom.copyInto(array, 0xC000, 0x4000 + headerSize, 0x8000 + headerSize)
                return BasicMemory(array)
            }
            else ->
                throw RomLoadError("Unsupported mapper type")
        }
    }
}


data class RomInformation(
    val romType: RomType,
    val verticalMirroring: Boolean,
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

