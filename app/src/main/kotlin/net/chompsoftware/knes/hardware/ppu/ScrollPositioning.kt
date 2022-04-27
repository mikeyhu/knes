package net.chompsoftware.knes.hardware.ppu

// Calculates the Y Tile when using vertical scrolling
@JvmInline
value class YPosition private constructor(private val tileY: Int) {
    fun isInOriginalBaseTable() = tileY < ROWS
    fun getTileY() = tileY % ROWS
    fun getNameTable(baseNameTable: Int): Int {
        return if (isInOriginalBaseTable()) {
            if (baseNameTable == 0x2800) 0x2400 else 0x2000
        } else {
            if (baseNameTable == 0x2800) 0x2000 else 0x2400
        }
    }

    companion object {
        operator fun invoke(scanlineRow: Int, scrollY: Int) = YPosition((scanlineRow + scrollY) / 8)
    }
}