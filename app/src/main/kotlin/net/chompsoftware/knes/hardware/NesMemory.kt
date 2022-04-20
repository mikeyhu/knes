package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.input.CONTROLLER0_POSITION
import net.chompsoftware.knes.hardware.input.CONTROLLER1_POSITION
import net.chompsoftware.knes.hardware.ppu.PPU_REG_OAM_DMA
import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

@ExperimentalUnsignedTypes
class NesMemory(
    private val rom: RomMapper,
    private val bus: Bus,
    private val failOnReadError: Boolean = true,
    private val failOnWriteError: Boolean = true
) : Memory {
    private val ram = UByteArray(0x800)

    override fun get(position: Int): UByte {
        return when (position) {
            in 0 until 0x2000 -> ram[mapToRam(position)]
            in 0x2000 until 0x4000 -> ppuRead(mapToPPU(position))
            in CONTROLLER0_POSITION..CONTROLLER1_POSITION -> bus.controllerInputRead(position)
            in 0x6000 until 0x8000 -> rom.getBatteryBackedRam(position)
            in 0x8000 until 0x10000 -> rom.getPrgRom(position)
            else ->
                if (failOnReadError) throw Error("NesMemory: (Read) Out of Range at ${position.toHex()}")
                else 0x0u
        }
    }

    override fun set(position: Int, value: UByte) {
        when (position) {
            in 0 until 0x2000 -> ram[mapToRam(position)] = value
            in 0x2000 until 0x4000 -> ppuWrite(mapToPPU(position), value)
            in CONTROLLER0_POSITION..CONTROLLER1_POSITION -> bus.controllerInputWrite(position, value)
            PPU_REG_OAM_DMA -> ppuOmaDmaWrite(value)
            in 0x6000 until 0x8000 -> rom.setBatteryBackedRam(position, value)
            else -> if (failOnWriteError) throw Error("NesMemory: (Write) Out of Range at ${position.toHex()}")
        }
    }

    private fun mapToRam(position: Int): Int {
        return position % 0x800
    }

    private fun mapToPPU(position: Int): Int {
        return position % 0x8
    }

    private fun ppuWrite(position: Int, value: UByte) {
        bus.ppuRegisterWrite(position, value)
    }

    private fun ppuRead(position: Int): UByte {
        return bus.ppuRegisterRead(position)
    }

    private fun ppuOmaDmaWrite(value: UByte) {
        println("OAM DMA WRITE : $value")
        val data = UByteArray(0x100)
        val startLocation = value.toInt().shl(8)
        for (i in 0..0xff) {
            data[i] = get(startLocation + i)
        }
        bus.oamDmaWrite(data)
    }
}
