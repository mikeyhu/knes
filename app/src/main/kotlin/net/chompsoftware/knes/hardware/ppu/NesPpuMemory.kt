package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

interface PpuMemory {
    fun get(position: Int): UByte
    fun set(position: Int, value: UByte)
    fun paletteTable(position: Int): Int
    fun oamDmaWrite(bytes: UByteArray, startPosition: Int)
    fun getOam(position: Int): UByte
    fun oam(): UByteArray
}

private const val PPU_CHROM_START = 0x0
private const val PPU_VRAM_START = 0x2000
private const val PPU_VRAM_SIZE = 0x800
private const val PALETTE_TABLE_START = 0x3F00
private const val PALETTE_SIZE = 0x20
private const val PALETTE_TABLE_END = PALETTE_TABLE_START + PALETTE_SIZE
private const val PPU_OAM_SIZE = 0x100

class NesPpuMemory(private val mapper: RomMapper) : PpuMemory {
    private val vram = UByteArray(PPU_VRAM_SIZE)
    private val oam = UByteArray(PPU_OAM_SIZE)
    private val palette = UByteArray(0x20)

    override fun get(position: Int): UByte {
        return when (position) {
            in PPU_CHROM_START until PPU_VRAM_START -> mapper.getChrRom(position)
            in PPU_VRAM_START until PALETTE_TABLE_START -> vram[mapToVRamPosition(position)]
            in PALETTE_TABLE_START until PALETTE_TABLE_END -> palette[position - PALETTE_TABLE_START]
            else ->
                throw Error("PpuMemory: (Read) Out of Range at ${position.toHex()}")
        }
    }

    override fun set(position: Int, value: UByte) {
        when (position) {
            in PPU_VRAM_START until PALETTE_TABLE_START -> vram[mapToVRamPosition(position)] = value
            in PALETTE_TABLE_START until PALETTE_TABLE_END -> palette[position - PALETTE_TABLE_START] = value
            else ->
                throw Error("PpuMemory: (Write) Out of Range at ${position.toHex()}")
        }
    }

    override fun paletteTable(position: Int): Int {
        return palette[position].toInt()
    }

    override fun oamDmaWrite(bytes: UByteArray, startPosition: Int) {
        bytes.copyInto(oam, startPosition, 0, PPU_OAM_SIZE - startPosition)
    }

    override fun getOam(position: Int): UByte {
        return oam[position]
    }

    override fun oam(): UByteArray {
        return oam
    }

    fun getSlice(position: Int, size: Int): UByteArray {
        return when (position) {
            in 0 until PPU_VRAM_START - size -> mapper.getChrRomSlice(position, size)
            else ->
                throw Error("PpuMemory: (Read) Out of Range at ${position.toHex()}")
        }
    }

    private fun mapToVRamPosition(position: Int) = (position - 0x2000) % 0x800
}

/* PPU Memory Map
 0x0000 - 0x1FFF = Pattern Tables in CHR-ROM = IMPLEMENTED
 0x2000 - 0x3E99 = Name Tables (VRAM)
 0x3F00 - 0x3FFF = Palettes
 0x4000 - 0xFFFF = Mirrors of the above
 */
