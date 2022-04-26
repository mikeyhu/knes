package net.chompsoftware.knes.hardware.ppu


data class NesScrollStatus(
    private var nextIsX: Boolean = true,
    private var x: Int = 0,
    private var y: Int = 0
) {

    fun reset() {
        nextIsX = true
        x = 0
        y = 0
    }

    fun write(value: UByte) {
        if (nextIsX) {
            x = value.toInt()
        } else {
            y = value.toInt()
        }
        nextIsX = !nextIsX
        println(this)
    }

    fun getX() = x
    fun getY() = y
}