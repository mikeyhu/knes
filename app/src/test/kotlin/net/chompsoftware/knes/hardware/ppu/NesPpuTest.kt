package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.utilities.ubyteArrayOfSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.random.Random

class NesPpuTest {

    inner class FakePpuMemory() : PpuMemory {
        var oamDmaWriteBytesWritten: UByteArray? = null
        var oamDmaWriteStartPosition: Int? = null

        override fun get(position: Int): UByte {
            return when (position) {
                0x600 -> 0xFFu
                else -> 0x00u
            }
        }

        override fun set(position: Int, value: UByte) {}

        override fun paletteTable(position: Int) = 0

        override fun oamDmaWrite(bytes: UByteArray, startPosition: Int) {
            oamDmaWriteBytesWritten = bytes
            oamDmaWriteStartPosition = startPosition
        }

        override fun getOam(position: Int): UByte {
            fail("should not be used")
        }

        override fun oam(): UByteArray {
            return oamDmaWriteBytesWritten!!
        }
    }

    @Test
    fun `Can accept address messages from the bus and then read data back with double read`() {
        val ppu = NesPpu(FakePpuMemory())

        ppu.busMemoryWriteEvent(PPU_REG_ADDRESS, 0x06u)
        ppu.busMemoryWriteEvent(PPU_REG_ADDRESS, 0x00u)

        // initial read should return dummy value
        assertEquals((0x00u).toUByte(), ppu.busMemoryReadEvent(PPU_REG_DATA))
        // next read should return the real value
        assertEquals((0xFFu).toUByte(), ppu.busMemoryReadEvent(PPU_REG_DATA))
    }

    @Test
    fun `cpuTick will return true if the scanline counter returns true and NMI is not disabled`() {
        val scanlineCounter = ScanlineCounter(240, 340, {})
        val ppu = NesPpu(FakePpuMemory(), scanlineCounter)

        ppu.busMemoryWriteEvent(PPU_REG_CONTROLLER, 0x80u) // bit 7 on

        assertTrue(ppu.cpuTick({}))
    }

    @Test
    fun `cpuTick will return false if the scanline counter returns true and NMI is disabled`() {
        val scanlineCounter = ScanlineCounter(240, 340, {})
        val ppu = NesPpu(FakePpuMemory(), scanlineCounter)

        ppu.busMemoryWriteEvent(PPU_REG_CONTROLLER, 0x0u) // bit 7 off

        assertFalse(ppu.cpuTick({}))
    }

    @Test
    fun `cpuTick will call onNMIInterrupt when an NMI occurs even if NMI is disabled`() {
        val scanlineCounter = ScanlineCounter(240, 340, {})
        val ppu = NesPpu(FakePpuMemory(), scanlineCounter)

        var onNMIInterruptCalled = false
        ppu.busMemoryWriteEvent(PPU_REG_CONTROLLER, 0x0u) // bit 7 off

        ppu.cpuTick({ onNMIInterruptCalled = true })
        assertTrue(onNMIInterruptCalled)
    }

    @Test
    fun `reading status will return inVBlank false if it's not been set`() {
        val ppu = NesPpu(FakePpuMemory())
        val expectedStatus: UByte = 0u

        val status = ppu.busMemoryReadEvent(PPU_REG_STATUS)

        assertEquals(expectedStatus, status)
    }

    @Test
    fun `reading status will return inVBlank true if it has been set by a cpuTick`() {
        val ppu = NesPpu(FakePpuMemory())
        ppu.scanlineCounter.currentScanline = 239
        ppu.scanlineCounter.currentScanlinePosition = 340
        ppu.oamDmaWrite(Random.ubyteArrayOfSize(0x100))
        ppu.cpuTick {}

        val expectedStatus: UByte = 0x80u

        val status = ppu.busMemoryReadEvent(PPU_REG_STATUS)

        assertEquals(expectedStatus, status)
    }

    @Test
    fun `reading status will return inVBlank false once it's been read`() {
        val ppu = NesPpu(FakePpuMemory())
        ppu.scanlineCounter.currentScanline = 239
        ppu.scanlineCounter.currentScanlinePosition = 340
        ppu.oamDmaWrite(Random.ubyteArrayOfSize(0x100))
        ppu.cpuTick {}

        val expectedStatus: UByte = 0u

        ppu.busMemoryReadEvent(PPU_REG_STATUS)
        val status = ppu.busMemoryReadEvent(PPU_REG_STATUS)

        assertEquals(expectedStatus, status)
    }

    @Test
    fun `Oam dma writes get written to memory`() {
        val ppuMemory = FakePpuMemory()
        val ppu = NesPpu(ppuMemory)

        val expectedBytesWritten = Random.ubyteArrayOfSize(0x100)
        ppu.oamDmaWrite(expectedBytesWritten)

        assertEquals(expectedBytesWritten, ppuMemory.oamDmaWriteBytesWritten)
        assertEquals(0, ppuMemory.oamDmaWriteStartPosition)
    }

    @Test
    fun `Oam dma writes get written from the specified position`() {
        val ppuMemory = FakePpuMemory()
        val ppu = NesPpu(ppuMemory)

        val expectedBytesWritten = Random.ubyteArrayOfSize(0x100)

        ppu.busMemoryWriteEvent(PPU_REG_OAM_ADDRESS, 0x80u)
        ppu.oamDmaWrite(expectedBytesWritten)

        assertEquals(expectedBytesWritten, ppuMemory.oamDmaWriteBytesWritten)
        assertEquals(0x80, ppuMemory.oamDmaWriteStartPosition)
    }

    @Nested
    inner class ScanlineCounterTest {
        @Test
        fun `ScanlineCounter raises NMI Interrupt on change to scanline 241 and then switches back to 0 or scanline 262`() {
            var scanlineFinishedCalled = 0
            val counter = ScanlineCounter() {
                scanlineFinishedCalled++
                assertTrue(it < 240)
            }

            for (i in 0 until (PPU_SCANLINE_SIZE * PPU_SCANLINE_NMI_INTERRUPT) - 3 step 3) {
                assertFalse(counter.cpuCycle(), "should have been false for ppuTick $i")
            }
            assertTrue(counter.cpuCycle())
            assertEquals(240, scanlineFinishedCalled)
            for (i in 0 until (PPU_SCANLINE_SIZE * PPU_SCANLINE_FRAME) - 3 step 3) {
                assertFalse(counter.cpuCycle(), "should have been false for ppuTick $i")
                assertTrue(counter.currentScanline < PPU_SCANLINE_FRAME)
            }
            assertTrue(counter.cpuCycle())
        }
    }

    @Nested
    inner class HorizontalMirroringLocationTest {
        @ParameterizedTest
        @CsvSource(
            "0x2000,  0,   0, 0, 0x2000", // top left
            "0x2000,  1,   0, 0, 0x2001",
            "0x2000,  0,   8, 0, 0x2020",
            "0x2000, 31, 232, 0, 0x23bf", // bottom right
        )
        fun `Items in 0x2000 map directly back to position 0x2000`(
            baseNametable: Int,
            tileX: Int,
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalMirroringPosition(baseNametable, tileX, scanlineRow, scrollY)
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @CsvSource(
            "0x2800,  0,   0, 0, 0x2400", // top left
            "0x2800,  1,   0, 0, 0x2401",
            "0x2800,  0,   8, 0, 0x2420",
            "0x2800, 31, 232, 0, 0x27bf", // bottom right
        )
        fun `Items in 0x2800 map directly back to position 0x2400`(
            baseNametable: Int,
            tileX: Int,
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalMirroringPosition(baseNametable, tileX, scanlineRow, scrollY)
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @CsvSource(
            "0x2800,  0,   0, 1, 0x2400", // still top left
            "0x2800,  0,   0, 7, 0x2400", // still top left
            "0x2800,  0,   0, 8, 0x2420", // top row down left
            "0x2800,  1,   0, 8, 0x2421",
            "0x2800,  0,   4, 4, 0x2420",
            "0x2800,  0,   8, 8, 0x2440",
            "0x2800, 31, 224, 8, 0x27bf", // second row from bottom
        )
        fun `The scrollY increments the tile selected within the table`(
            baseNametable: Int,
            tileX: Int,
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalMirroringPosition(baseNametable, tileX, scanlineRow, scrollY)
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @CsvSource(
            "0x2800, 0, 232, 8, 0x2000",
        )
        fun `The scrollY wraps round to the alternate table`(
            baseNametable: Int,
            tileX: Int,
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalMirroringPosition(baseNametable, tileX, scanlineRow, scrollY)
            assertEquals(expected, result)
        }
    }

    @Nested
    inner class HorizontalPixelOffsetTest {
        @ParameterizedTest
        @CsvSource(
            "0, 0, 0",
            "1, 0, 1",
            "8, 0, 0",
            "9, 0, 1"
        )
        fun `decides on the correct row within the tile when there is no scroll`(
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalPixelOffset(scanlineRow, scrollY)
            assertEquals(expected, result)
        }

        @ParameterizedTest
        @CsvSource(
            "0, 1, 1",
            "1, 1, 2",
            "0, 8, 0",
            "7, 1, 0",
            "8, 1, 1"
        )
        fun `decides on the correct row within the tile when there is a scroll`(
            scanlineRow: Int,
            scrollY: Int,
            expected: Int
        ) {
            val result = horizontalPixelOffset(scanlineRow, scrollY)
            assertEquals(expected, result)
        }
    }
}



