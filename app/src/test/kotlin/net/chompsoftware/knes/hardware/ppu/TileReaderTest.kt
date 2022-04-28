package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class TileReaderTest {

    @Test
    fun `Throws error if the input array is not 16 bytes in length`() {
        assertThrows<TileError> { TileReader.getTileFromMemory(UByteArray(15)) }
        assertThrows<TileError> { TileReader.getTileFromMemory(UByteArray(17)) }
    }

    @Test
    fun `Returns a Tile with an array that's 64 bytes long`() {
        val tile = TileReader.getTileFromMemory(UByteArray(16))
        assertEquals(64, tile.pixels.size)
    }

    @Test
    fun `Returns a Tile with data read from the input array`() {

        // example from : https://bugzmanov.github.io/nes_ebook/chapter_6_3.html
        val input = listOf(
            //left
            128 + 64 + 32,
            128 + 64,
            128,
            128 + 64 + 32 + 16 + 8 + 4,
            128,
            128 + 64,
            0,
            32,
            //right
            0,
            32,
            64 + 32,
            0,
            128 + 64 + 32 + 16,
            128 + 64 + 32 + 16 + 8 + 4,
            128 + 64 + 32 + 16 + 8 + 4 + 2,
            128 + 64 + 32 + 16 + 8 + 4 + 2
        ).map { it.toUByte() }.toUByteArray()

        val tile = TileReader.getTileFromMemory(input)

        val output = listOf<Byte>(
            1, 1, 1, 0, 0, 0, 0, 0,
            1, 1, 2, 0, 0, 0, 0, 0,
            1, 2, 2, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 1, 0, 0,
            3, 2, 2, 2, 0, 0, 0, 0,
            3, 3, 2, 2, 2, 2, 0, 0,
            2, 2, 2, 2, 2, 2, 2, 0,
            2, 2, 3, 2, 2, 2, 2, 0
        ).toByteArray()

        assertArrayEquals(output, tile.pixels)
    }

    @Nested
    inner class TileRowTest {
        @ParameterizedTest
        @CsvSource(
            "0x00, 0x00, 0x00",
            "0x01, 0x00, 0x01",
            "0x00, 0x01, 0x02",
            "0x01, 0x01, 0x03",

            "0x02, 0x00, 0x04",
            "0x00, 0x02, 0x08",
            "0x02, 0x02, 0x0c",

            "0x04, 0x00, 0x10",
            "0x00, 0x04, 0x20",
            "0x04, 0x04, 0x30",

            "0x08, 0x00, 0x40",
            "0x00, 0x08, 0x80",
            "0x08, 0x08, 0xc0",

            "0x10, 0x00, 0x100",
            "0x00, 0x10, 0x200",
            "0x10, 0x10, 0x300",

            "0x20, 0x00, 0x400",
            "0x00, 0x20, 0x800",
            "0x20, 0x20, 0xc00",

            "0x40, 0x00, 0x1000",
            "0x00, 0x40, 0x2000",
            "0x40, 0x40, 0x3000",

            "0x80, 0x00, 0x4000",
            "0x00, 0x80, 0x8000",
            "0x80, 0x80, 0xc000",

            "0xff, 0xff, 0xffff"
        )
        fun `Can convert two tileBytes into palette information in an Int`(tileA: Int, tileB: Int, expected: Int) {
            val result = toTileRow(tileA, tileB)
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @CsvSource(
            "  0x00, 7, 0",
            "  0x01, 7, 1",
            "  0x02, 7, 2",
            "  0x03, 7, 3",
            "  0x00, 6, 0",
            "  0x01, 6, 0",
            "  0x02, 6, 0",
            "  0x03, 6, 0",
            "  0x04, 6, 1",
            "  0x08, 6, 2",
            "  0x0c, 6, 3",
            "0xffff, 0, 3",
            "0xffff, 1, 3",
            "0xffff, 2, 3",
            "0xffff, 3, 3",
            "0xffff, 4, 3",
            "0xffff, 5, 3",
            "0xffff, 6, 3",
            "0xffff, 7, 3"
        )
        fun `Retrieve a pattern from a larger Int`(tile: Int, position: Int, expected: Int) {
            val result = tile.pixelFor(position)
            assertEquals(expected, result)
        }
    }
}