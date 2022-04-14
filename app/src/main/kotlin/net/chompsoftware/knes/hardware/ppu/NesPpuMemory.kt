package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.toHex

interface PpuMemory {
    fun get(position: Int): UByte
    fun set(position: Int, value: UByte)
    fun paletteTable(position: Int): Int
    fun oamDmaWrite(bytes: UByteArray, startPosition: Int)
    fun getOam(position: Int): UByte
}

private const val PPU_CHROM_START = 0x0
private const val PPU_VRAM_START = 0x2000
private const val PPU_VRAM_SIZE = 0x2000
private const val PPU_VRAM_END = PPU_VRAM_START + PPU_VRAM_SIZE
private const val PALETTE_TABLE_START = 0x3F00
private const val PPU_OAM_SIZE = 0x100

class NesPpuMemory(val mapper: RomMapper) : PpuMemory {
    private val vram = UByteArray(PPU_VRAM_SIZE)
    private val oam = UByteArray(PPU_OAM_SIZE)

    override fun get(position: Int): UByte {
        return when (position) {
            in PPU_CHROM_START until PPU_VRAM_START -> mapper.getChrRom(position)
            in PPU_VRAM_START until PPU_VRAM_END -> vram[position - PPU_VRAM_START]
            else ->
                throw Error("PpuMemory: (Read) Out of Range at ${position.toHex()}")
        }
    }

    override fun set(position: Int, value: UByte) {
        when (position) {
            in PPU_VRAM_START until PPU_VRAM_END -> vram[position - PPU_VRAM_START] = value
            else ->
                throw Error("PpuMemory: (Write) Out of Range at ${position.toHex()}")
        }
    }

    override fun paletteTable(position: Int): Int {
        return vram[position + PALETTE_TABLE_START - PPU_VRAM_START].toInt() // 0x3F00 - 0x2000
    }

    override fun oamDmaWrite(bytes: UByteArray, startPosition: Int) {
        bytes.copyInto(oam, 0, 0, PPU_OAM_SIZE)
    }

    override fun getOam(position: Int): UByte {
        return oam[position]
    }

    fun getSlice(position: Int, size: Int): UByteArray {
        return when (position) {
            in 0 until PPU_VRAM_START - size -> mapper.getChrRomSlice(position, size)
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
