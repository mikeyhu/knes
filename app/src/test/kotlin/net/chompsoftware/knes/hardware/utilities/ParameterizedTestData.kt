package net.chompsoftware.knes.hardware.utilities

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
                ComparisonWithNegativeZeroCarryCheck(0x00u, 0x80u, true, false, true),
            )
        }
    }
}


