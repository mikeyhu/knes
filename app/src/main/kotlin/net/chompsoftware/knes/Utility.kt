package net.chompsoftware.knes


fun UByte.toHex() = "0x" + this.toString(16)
fun UInt.toHex() = "0x" + this.toString(16)
fun Int.toHex() = "0x" + this.toString(16)

fun toUInt16(c: UByte, c2: UByte) = c2.toUInt().shl(8).or(c.toUInt())
fun toInt16(c: UByte, c2: UByte) = toUInt16(c, c2).toInt()

@ExperimentalUnsignedTypes
fun setupMemory(vararg bytes: UByte, size: Int = 0x8000): UByteArray {
    val array = UByteArray(size)
    bytes.copyInto(array, 0, 0, bytes.size)
    return array
}