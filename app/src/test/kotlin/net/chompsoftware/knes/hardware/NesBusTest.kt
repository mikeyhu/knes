package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.input.Controller
import net.chompsoftware.knes.hardware.input.ControllerInput
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

    inner class FakeControllerInput(val valueToRead: UByte = 0xffu) : ControllerInput {
        var writePositionReceived: Int? = null
        var writeValueReceived: UByte? = null
        var readPositionReceived: Int? = null

        override fun write(position: Int, value: UByte) {
            writePositionReceived = position
            writeValueReceived = value
        }

        override fun read(position: Int): UByte {
            readPositionReceived = position
            return valueToRead
        }

        override fun getControllerO(): Controller {
            fail("should not be used")
        }

        override fun getController1(): Controller {
            fail("should not be used")
        }

    }

    @Test
    fun `Can invoke a callback if it registers for the CpuSuspend functionality`() {
        val bus = NesBus(FakePpu(), FakeControllerInput())

        var valueFromCallback: Int? = null

        bus.registerCallbackForCpuSuspend { valueFromCallback = it }

        bus.performCallbackForCpuSuspend(0xff)

        assertEquals(0xff, valueFromCallback)
    }

    @Test
    fun `OAM DMA write will write to the PPU and call the cpuSuspend function`() {
        val ppu = FakePpu()
        val bus = NesBus(ppu, FakeControllerInput())

        var valueFromCallback: Int? = null
        val expectedUByteArray = Random.ubyteArrayOfSize(0x100)

        bus.registerCallbackForCpuSuspend { valueFromCallback = it }

        bus.oamDmaWrite(expectedUByteArray)

        assertEquals(OAM_CPU_SUSPEND_CYCLES, valueFromCallback)
        assertEquals(expectedUByteArray, ppu.oamDmaWritten)
    }

    @Test
    fun `Can write to controller input`() {
        val controllerInput = FakeControllerInput()
        val bus = NesBus(FakePpu(), controllerInput)

        bus.controllerInputWrite(0x4016, 0x1u)

        assertEquals(0x4016, controllerInput.writePositionReceived)
        assertEquals((0x1u).toUByte(), controllerInput.writeValueReceived)
    }

    @Test
    fun `Can read from controller input`() {
        val controllerInput = FakeControllerInput()
        val bus = NesBus(FakePpu(), controllerInput)

        val result = bus.controllerInputRead(0x4016)

        assertEquals(0x4016, controllerInput.readPositionReceived)
        assertEquals((0xffu).toUByte(), result)
    }
}