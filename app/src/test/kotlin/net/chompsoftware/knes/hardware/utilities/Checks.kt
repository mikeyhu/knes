package net.chompsoftware.knes.hardware.utilities

data class InputWithNegativeZeroCheck(
    val input: UByte,
    val negativeFlag: Boolean,
    val zeroFlag: Boolean
)

data class ComparisonWithNegativeZeroCarryCheck(
    val input: UByte,
    val existing: UByte,
    val negativeFlag: Boolean,
    val zeroFlag: Boolean,
    val carryFlag: Boolean
)

data class AddWithCarryCheck(
    val aReg: UByte,
    val memory: UByte,
    val carry: Boolean,
    val expected: UByte,
    val negativeFlag: Boolean,
    val overflowFlag: Boolean,
    val carryFlag: Boolean,
    val zeroFlag: Boolean
)

data class RegisterMemoryExpectedCheck(
    val aReg: UByte,
    val memory: UByte,
    val expected: UByte,
    val negativeFlag: Boolean,
    val zeroFlag: Boolean,
    val overflowFlag: Boolean = false
)

data class ShiftCheck(
    val input: UByte,
    val output: UByte,
    val negativeFlag: Boolean = false,
    val carryFlag: Boolean = false,
    val zeroFlag: Boolean = false,
    val carryIn: Boolean = false
)