package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.setupMemory
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class CpuTest {
    @Test
    fun `processInstruction can find the right instruction`() {
        val memory = Memory(setupMemory(LDA_I, 0x01u))
        val cpu = CpuState(0, 0x0u)

        val effects = processInstruction(cpu, memory).toList()

        assertEquals(2, effects.size)
        assertEquals(0x01u, cpu.aReg)
        assertEquals(0x02, cpu.programCounter)
    }

    @Test
    fun `processInstruction can find the right instruction with the right addressing`() {
        val memory = Memory(setupMemory(LDA_AB, 0x3u, 0x0u, 0x99u))
        val cpu = CpuState(0, 0x0u)

        val effects = processInstruction(cpu, memory).toList()

        assertEquals(4, effects.size)
        assertEquals(0x99u, cpu.aReg)
        assertEquals(3, cpu.programCounter)
    }

}


