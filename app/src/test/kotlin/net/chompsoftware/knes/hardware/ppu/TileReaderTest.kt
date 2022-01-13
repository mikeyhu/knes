package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


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
}