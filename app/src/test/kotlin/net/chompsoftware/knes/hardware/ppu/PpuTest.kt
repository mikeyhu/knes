package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PpuTest {

    inner class FakePpuMemory() : PpuMemory {
        override fun get(position: Int): UByte {
            return when (position) {
                0x600 -> 0xFFu
                else -> 0x00u
            }
        }

        override fun set(position: Int, value: UByte) {
            TODO("Not yet implemented")
        }

    }

    @Test
    fun `Can accept address messages from the bus and then read data back with double read`() {
        val ppu = Ppu(FakePpuMemory())

        ppu.busMemoryWriteEvent(PPU_REG_ADDRESS, 0x06u)
        ppu.busMemoryWriteEvent(PPU_REG_ADDRESS, 0x00u)

        // initial read should return dummy value
        assertEquals((0x00u).toUByte(), ppu.busMemoryReadEvent(PPU_REG_DATA))
        // next read should return the real value
        assertEquals((0xFFu).toUByte(), ppu.busMemoryReadEvent(PPU_REG_DATA))
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

}



