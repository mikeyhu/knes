package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

class NesMemory(
    private val rom: RomMapper,
    private val failOnReadError: Boolean = true,
    private val failOnWriteError: Boolean = true
) : Memory {
    private val ram = UByteArray(0x800)
    private val ppu = UByteArray(0x8)

    override fun get(position: Int): UByte {
        return when (position) {
            in 0 until 0x2000 -> ram[mapToRam(position)]
            in 0x2000 until 0x4000 -> ppu[mapToPPU(position)]
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
            in 0x2000 until 0x4000 -> ppu[mapToPPU(position)] = value
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
}