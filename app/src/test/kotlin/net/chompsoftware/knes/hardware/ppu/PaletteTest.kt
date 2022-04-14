package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PaletteTest {

    /*
        Palettes are stored at PALETTE_TABLE_START
        4 background palettes
        4 sprite palettes
        Each palette contains 3 colours + Universal background stored at 0x3F00


        Background Palettes:
        Last 64 bytes of each nametable controls tile color

     */
    val testTileX = 8
    val testTileY = 4

    inner class FakePpuMemory() : PpuMemory {
        override fun get(position: Int): UByte {
            return when (position) {
                0x2000 + 0x3c0 + (testTileY / 4 * 8) + (testTileX / 4) -> 0xD8u // 11 01 10 00
                else -> 0x00u
            }
        }

        override fun set(position: Int, value: UByte) {}

        override fun paletteTable(position: Int) = 0
        override fun oamDmaWrite(bytes: UByteArray, startPosition: Int) {
            fail("should not be used")
        }

        override fun getOam(position: Int): UByte {
            fail("should not be used")
        }

    }

    @ParameterizedTest
    @CsvSource(
        "0, 0, 0",
        "0, 1, 0",
        "1, 0, 0",
        "1, 1, 0",
        "2, 0, 2",
        "2, 1, 2",
        "3, 0, 2",
        "3, 1, 2",
        "0, 2, 1",
        "1, 2, 1",
        "0, 3, 1",
        "1, 3, 1",
        "2, 2, 3",
        "3, 2, 3",
        "2, 3, 3",
        "3, 3, 3",
    )
    fun `can generate a palette of 4 colours by retrieving data from the PPU Palette memory`(
        offsetX: Int,
        offsetY: Int,
        expectedPalette: Int
    ) {
        val memory = FakePpuMemory()
        val result = selectPaletteNumber(memory, testTileX + offsetX, testTileY + offsetY)
        assertEquals(expectedPalette, result)
    }
}