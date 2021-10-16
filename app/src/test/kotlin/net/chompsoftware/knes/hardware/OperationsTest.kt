package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class OperationsTest {


    @Test
    fun `NOP - No Operation`() {
        val memory = BasicMemory(setupMemory(NOP))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, NOP)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
        }
    }
}