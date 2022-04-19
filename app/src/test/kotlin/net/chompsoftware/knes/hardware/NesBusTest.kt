package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.OAM_CPU_SUSPEND_CYCLES
import net.chompsoftware.knes.hardware.ppu.Ppu
import net.chompsoftware.knes.hardware.utilities.ubyteArrayOfSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.awt.image.BufferedImage
import kotlin.random.Random


class NesBusTest {

    inner class FakePpu() : Ppu {
        var oamDmaWritten: UByteArray? = null
        override fun cpuTick(onNMIInterrupt: () -> Unit): Boolean {
            fail("should not be used")
        }

        override fun getFinishedImage(): BufferedImage {
            fail("should not be used")
        }

        override fun busMemoryWriteEvent(position: Int, value: UByte) {
            fail("should not be used")
        }

        override fun busMemoryReadEvent(position: Int): UByte {
            fail("should not be used")
        }

        override fun oamDmaWrite(bytes: UByteArray) {
            oamDmaWritten = bytes
        }
    }

    @Test
    fun `Can invoke a callback if it registers for the CpuSuspend functionality`() {
        val bus = NesBus(FakePpu())

        var valueFromCallback: Int? = null

        bus.registerCallbackForCpuSuspend { valueFromCallback = it }

        bus.performCallbackForCpuSuspend(0xff)

        assertEquals(0xff, valueFromCallback)
    }

    @Test
    fun `OAM DMA write will write to the PPU and call the cpuSuspend function`() {
        val ppu = FakePpu()
        val bus = NesBus(ppu)

        var valueFromCallback: Int? = null
        val expectedUByteArray = Random.ubyteArrayOfSize(0x100)

        bus.registerCallbackForCpuSuspend { valueFromCallback = it }

        bus.oamDmaWrite(expectedUByteArray)

        assertEquals(OAM_CPU_SUSPEND_CYCLES, valueFromCallback)
        assertEquals(expectedUByteArray, ppu.oamDmaWritten)
    }
}