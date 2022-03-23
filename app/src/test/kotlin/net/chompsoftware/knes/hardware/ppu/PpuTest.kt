package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PpuTest {

    inner class FakePpuMemory() : PpuMemory {
        override fun get(position: Int): UByte {
            return when (position) {
                0x600 -> 0xFFu
                else -> 0x00u
            }
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
}



