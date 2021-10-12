package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.Activity.MemoryReadActivity
import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import kotlin.test.Test

@ExperimentalUnsignedTypes
class LoadOperationsTest {
    @Test
    fun `LDA Immediate`() {
        val memory = BasicMemory(setupMemory(LDA_I, 0x01u))
        val cpu = CpuState(0, 0x0u)

        val interrogator = HardwareInterrogator(cpu, memory)

        interrogator.processInstruction()

        interrogator.assertCpuState {
            assertProgramCounter(2)
            assertAReg(0x1u)
        }

        interrogator.assertCycleLog {
            cycle {
                activity(MemoryReadActivity(0, LDA_I))
            }
            cycle {
                activity(MemoryReadActivity(1, 0x01u))
            }
        }
    }
}