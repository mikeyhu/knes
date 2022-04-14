package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.Ppu
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.awt.image.BufferedImage


class FakePpu(private val cpuTickReturn: Boolean = false) : Ppu {
    var timesPpuCalled = 0
    override fun cpuTick(onNMIInterrupt: () -> Unit): Boolean {
        timesPpuCalled++
        return cpuTickReturn
    }

    override fun getBufferedImage(): BufferedImage {
        fail("should not be called")
    }

    override fun busMemoryWriteEvent(position: Int, value: UByte) {
        fail("should not be used")
    }

    override fun busMemoryReadEvent(position: Int): UByte {
        fail("should not be used")
    }

    override fun oamDmaWrite(bytes: UByteArray) {
        fail("should not be used")
    }
}

class FakeOperation(private val effectPipelineReturn: EffectPipeline? = null) : EffectPipeline {
    var capturedCpuState: CpuState? = null
    var capturedMemory: Memory? = null
    var capturedOperationState: OperationState? = null
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        capturedCpuState = cpuState
        capturedMemory = memory
        capturedOperationState = operationState
        return effectPipelineReturn ?: this
    }
}

class FakeBus : Bus {
    override fun ppuRegisterWrite(position: Int, value: UByte) {
        fail("should not be used")
    }

    override fun ppuRegisterRead(position: Int): UByte {
        fail("should not be used")
    }

    override fun performCallbackForCpuSuspend(cycles: Int) {
        fail("should not be used")
    }

    override fun registerCallbackForCpuSuspend(callback: (Int) -> Unit) {
        fail("should not be used")
    }

    override fun oamDmaWrite(bytes: UByteArray) {
        fail("should not be used")
    }

}


class CycleCoordinatorTest {

    private val fakeMemory = BasicMemory(setupMemory(size = 0x10000)).also {
        it[0xfffc] = 0x34u
        it[0xfffd] = 0x12u
    }

    @Test
    fun `Will send a cycle to the PPU`() {
        val fakeOperation = FakeOperation()
        val fakePpu = FakePpu()
        val cycleCoordinator = CycleCoordinator(fakeOperation, fakePpu, fakeMemory, FakeBus())

        cycleCoordinator.cycle {}

        assertTrue(fakePpu.timesPpuCalled == 1)
    }

    @Test
    fun `Will send a cycle to the PPU and the CPU`() {
        val fakeOperation = FakeOperation()
        val fakePpu = FakePpu()

        val cycleCoordinator = CycleCoordinator(fakeOperation, fakePpu, fakeMemory, FakeBus())

        cycleCoordinator.cycle {}

        assertEquals(0x1234, fakeOperation.capturedCpuState?.programCounter)
        assertEquals(0, fakeOperation.capturedOperationState?.pipelinePosition)
    }

    @Test
    fun `Will raise NMI Interrupt with CPU if PPU returns true`() {
        val fakeOperation = FakeOperation()
        val fakePpu = FakePpu(cpuTickReturn = true)

        val cycleCoordinator = CycleCoordinator(fakeOperation, fakePpu, fakeMemory, FakeBus())

        cycleCoordinator.cycle {}

        assertTrue(fakeOperation.capturedCpuState!!.isNMIInterrupt)
    }

    @Test
    fun `Second Cycle will call an effectPipeline stored in the first`() {
        val fakeSecondOperation = FakeOperation()
        val fakeOperation = FakeOperation(fakeSecondOperation)
        val fakePpu = FakePpu()

        val cycleCoordinator = CycleCoordinator(fakeOperation, fakePpu, fakeMemory, FakeBus())

        cycleCoordinator.cycle {}
        cycleCoordinator.cycle {}

        assertNotNull(fakeOperation.capturedCpuState)
        assertNotNull(fakeSecondOperation.capturedCpuState)
    }
}
