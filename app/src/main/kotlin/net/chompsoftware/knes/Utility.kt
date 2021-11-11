package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.effects.CpuStatusComparisons

fun UByte.toHex() = "0x" + this.toString(16)
fun UInt.toHex() = "0x" + this.toString(16)
fun Int.toHex() = "0x" + this.toString(16)
fun UByte.toLogHex() = this.toString(16).uppercase()
fun UInt.toLogHex() = this.toString(16).uppercase()
fun Int.toLogHex() = this.toString(16).uppercase()

fun String.toHexUByte() = Integer.parseInt(this.removePrefix("0x").removeSuffix("u"), 16).toUByte()
fun String.toHexUInt() = Integer.parseInt(this.removePrefix("0x").removeSuffix("u"), 16).toUInt()


fun toUInt16(c: UByte, c2: UByte) = c2.toUInt().shl(8).or(c.toUInt())
fun toInt16(c: UByte, c2: UByte) = toUInt16(c, c2).toInt()

fun pageBoundaryCrossed(previous: Int, next: Int) = previous.shr(8) != next.shr(8)


@ExperimentalUnsignedTypes
fun setupMemory(vararg bytes: UByte, size: Int = 0x8000, memoryOffset: Int = 0): UByteArray {
    val array = UByteArray(size)
    bytes.copyInto(array, memoryOffset, 0, bytes.size)
    return array
}

fun UByte.isZero() = this == CpuStatusComparisons.ZERO_FLAG
fun UByte.isNegative() = (this and CpuStatusComparisons.NEGATIVE_FLAG) > 0u
fun UInt.isCarry() = this.shr(8) > 0u