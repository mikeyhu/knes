package net.chompsoftware.knes.hardware.ppu

import java.awt.Color
import java.awt.image.BufferedImage


class Tile(val pixels: ByteArray) {
    fun asBufferedImage(palette: Array<Color>): BufferedImage {
        if (palette.size != 4) {
            throw TileError("expected palette of 4 colors. Found ${palette.size}")
        }
        val img = BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
        for (y in 0..7) {
            for (x in 0..7) {
                img.setRGB(x, y, palette[pixels[x + (y * 8)].toInt()].rgb)
            }
        }
        return img
    }
}

class TileError(message: String) : Error(message)

object TileReader {

    fun getTileFromMemory(bytes: UByteArray): Tile {
        if (bytes.size != 16) {
            throw TileError("Input was ${bytes.size} but expected 16")
        }

        val output = ByteArray(64)

        for (j in 0..7) {
            for (i in 0..7) {
                output[i + (j * 8)] = (bytes[j].bitAsByte(7 - i) + (bytes[j + 8].bitAsByte(7 - i) * 2)).toByte()
            }
        }
        return Tile(output)
    }


    private fun UByte.bitAsByte(position: Int): Byte {
        return this.toUInt().shr(position).and(1u).toByte()
    }
}

typealias TileRow = Int

fun toTileRow(tileALine: Int, tileBLine: Int): TileRow {
    return partialRow(tileALine, tileBLine, 0x1) +
            partialRow(tileALine, tileBLine, 0x2) +
            partialRow(tileALine, tileBLine, 0x4) +
            partialRow(tileALine, tileBLine, 0x8) +
            partialRow(tileALine, tileBLine, 0x10) +
            partialRow(tileALine, tileBLine, 0x20) +
            partialRow(tileALine, tileBLine, 0x40) +
            partialRow(tileALine, tileBLine, 0x80)
}

fun partialRow(tileALine: Int, tileBLine: Int, position: Int): Int =
    (tileALine.and(position) + (tileBLine.and(position) shl 1)) * position

fun TileRow.pixelFor(position: Int): Int {
    return when (7 - position) {
        0 -> this.and(0x03)
        1 -> this.and(0x0c) shr 2
        2 -> this.and(0x30) shr 4
        3 -> this.and(0xc0) shr 6
        4 -> this.and(0x300) shr 8
        5 -> this.and(0xc00) shr 10
        6 -> this.and(0x3000) shr 12
        7 -> this.and(0xc000) shr 14
        else -> 0
    }
}
