package net.chompsoftware.knes.hardware

import java.util.stream.Stream


open class ParameterizedTestData {
    companion object {
        const val NEGATIVE_ZERO_CHECK = "{0}: negativeFlag=={1} zeroFlag=={2}"

        @JvmStatic
        fun checkNegativeZeroFlags(): Stream<InputWithNegativeZeroCheck> {
            return Stream.of(
                InputWithNegativeZeroCheck(0x10u, false, false),
                InputWithNegativeZeroCheck(0x80u, true, false),
                InputWithNegativeZeroCheck(0x0u, false, true)
            )
        }

        @JvmStatic
        fun checkComparisonNegativeZeroCarryFlags(): Stream<ComparisonWithNegativeZeroCarryCheck> {
            return Stream.of(
                ComparisonWithNegativeZeroCarryCheck(0x10u, 0x11u, false, false, true),
                ComparisonWithNegativeZeroCarryCheck(0x10u, 0x10u, false, true, true),
                ComparisonWithNegativeZeroCarryCheck(0x12u, 0x11u, true, false, false),
            )
        }

        @JvmStatic
        fun checkAddWithCarryFlags(): Stream<AddWithCarryCheck> {
            return Stream.of(
                AddWithCarryCheck(0x0fu, 0x01u, false, 0x10u, false, false, false),
                AddWithCarryCheck(0x7fu, 0x0u, false, 0x7fu, false, false, false),
                AddWithCarryCheck(0x7fu, 0x0u, true, 0x80u, true, true, false),
                AddWithCarryCheck(0x80u, 0x0u, false, 0x80u, true, false, false),
            )
        }

        @JvmStatic
        fun checkExclusiveOrFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0x0fu, 0xffu, 0xf0u, true, false),
                RegisterMemoryExpectedCheck(0xf0u, 0xffu, 0x0fu, false, false),
                RegisterMemoryExpectedCheck(0xffu, 0xffu, 0x00u, false, true),
            )
        }

        @JvmStatic
        fun checkOrFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0x0fu, 0xf0u, 0xffu, true, false),
                RegisterMemoryExpectedCheck(0xf0u, 0xffu, 0xffu, true, false),
                RegisterMemoryExpectedCheck(0x00u, 0x00u, 0x00u, false, true),
                RegisterMemoryExpectedCheck(0x0fu, 0x10u, 0x1fu, false, false),
            )
        }

        @JvmStatic
        fun checkBitFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0xffu, 0xf0u, 0xffu, true, false, true),
                RegisterMemoryExpectedCheck(0xffu, 0xffu, 0xffu, true, false, true),
                RegisterMemoryExpectedCheck(0x01u, 0x01u, 0x01u, false, false, false),
                RegisterMemoryExpectedCheck(0x10u, 0x01u, 0x10u, false, true, false),
                RegisterMemoryExpectedCheck(0x01u, 0xffu, 0x01u, true, false, true),
                RegisterMemoryExpectedCheck(0x01u, 0x10u, 0x01u, false, true, false),
            )
        }

        @JvmStatic
        fun checkAslFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0xf2u, 0xe4u, true, true, false),
                ShiftCheck(0x0u, 0x0u, false, false, true),
            )
        }

        @JvmStatic
        fun checkLsrFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x81u, 0x40u, false, true, false),
                ShiftCheck(0x80u, 0x40u, false, false, false),
                ShiftCheck(0xf2u, 0x79u, false, false, false),
            )
        }

        @JvmStatic
        fun checkRolFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x81u, 0x2u, false, true, false),
                ShiftCheck(0x81u, 0x3u, false, true, false, carryIn = true),
                ShiftCheck(0x00u, 0x1u, false, false, false, carryIn = true),
                ShiftCheck(0x80u, 0x0u, false, true, true),
                ShiftCheck(0x40u, 0x80u, true, false, false),
            )
        }

        @JvmStatic
        fun checkRorFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x3u, 0x1u, false, true, false),
                ShiftCheck(0x1u, 0x0u, false, true, true),
                ShiftCheck(0x0u, 0x80u, true, false, false, carryIn = true),
                ShiftCheck(0x1u, 0x80u, true, true, false, carryIn = true),
            )
        }
    }
}

data class InputWithNegativeZeroCheck(val input: UByte, val negativeFlag: Boolean, val zeroFlag: Boolean)

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
    val carryFlag: Boolean
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
    val negativeFlag: Boolean,
    val carryFlag: Boolean,
    val zeroFlag: Boolean,
    val carryIn: Boolean = false
)
