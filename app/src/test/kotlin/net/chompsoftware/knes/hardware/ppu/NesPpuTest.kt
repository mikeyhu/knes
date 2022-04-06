package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NesPpuTest {

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

        override fun paletteTable(position: Int): Int {
            TODO("Not yet implemented")
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



