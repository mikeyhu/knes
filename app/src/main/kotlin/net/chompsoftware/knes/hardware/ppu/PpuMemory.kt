package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

@ExperimentalUnsignedTypes
class PpuMemory(val mapper: RomMapper) {

    fun get(position: Int): UByte {
        return when (position) {
            in 0 until 0x2000 -> mapper.getChrRom(position)
            else ->
                throw Error("PpuMemory: (Read) Out of Range at ${position.toHex()}")
        }
    }

    fun getSlice(position: Int, size: Int): UByteArray {
        return when (position) {
            in 0 until 0x2000 - size -> mapper.getChrRomSlice(position, size)
            else ->
                throw Error("PpuMemory: (Read) Out of Range at ${position.toHex()}")
        }
    }

}