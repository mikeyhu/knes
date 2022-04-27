package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class ScrollPositioningTest {
    @Nested
    inner class YPositionTest {
        @ParameterizedTest
        @CsvSource(
            "  0, 0,  0",
            "  7, 0,  0",
            "  7, 1,  1",
            "  8, 0,  1",
            "  9, 0,  1",
            "239, 0, 29",
        )
        fun `returns the tile yPosition in the same nametable`(
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val yPosition = YPosition(scanlineRow, scrollY)
            Assertions.assertEquals(expected, yPosition.getTileY())
            Assertions.assertTrue(yPosition.isInOriginalBaseTable())
        }

        @ParameterizedTest
        @CsvSource(
            "239, 1, 0"
        )
        fun `returns the tile yPosition in the other nametable`(
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val yPosition = YPosition(scanlineRow, scrollY)
            Assertions.assertEquals(expected, yPosition.getTileY())
            Assertions.assertFalse(yPosition.isInOriginalBaseTable())
        }

        @ParameterizedTest
        @CsvSource(
            "239, 0, 0x2000, 0x2000",
            "239, 0, 0x2800, 0x2400",
            "239, 1, 0x2000, 0x2400",
            "239, 1, 0x2800, 0x2000"
        )
        fun `returns the nametable to use when the base is passed in`(
            scanlineRow: Int,
            scrollY: Int,
            baseNameTable: Int,
            expectedNameTable: Int
        ) {
            val yPosition = YPosition(scanlineRow, scrollY)
            Assertions.assertEquals(expectedNameTable, yPosition.getNameTable(baseNameTable))
        }
    }
}