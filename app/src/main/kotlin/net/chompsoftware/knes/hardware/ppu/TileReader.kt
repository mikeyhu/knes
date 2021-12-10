package net.chompsoftware.knes.hardware.ppu


class Tile(val pixels: ByteArray)

class TileError(message: String) : Error(message)

@ExperimentalUnsignedTypes
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