package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

interface PpuMemory {
    fun get(position: Int): UByte
}

class NesPpuMemory(val mapper: RomMapper) : PpuMemory {
    override fun get(position: Int): UByte {
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

/* PPU Memory Map
 0x0000 - 0x1FFF = Pattern Tables in CHR-ROM = IMPLEMENTED
 0x2000 - 0x3E99 = Name Tables (VRAM)
 0x3F00 - 0x3FFF = Palettes
 0x4000 - 0xFFFF = Mirrors of the above
 */
