package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test


class JumpOperationsTest {
    @Test
    fun `JMP - Jump to absolute position`() {
        val memory = BasicMemory(setupMemory(JMP_AB, 0x34u, 0x12u))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, JMP_AB)
            }
            cycle {
                memoryRead(1, 0x34u)
            }
            cycle {
                memoryRead(2, 0x12u)
            }
        }

        interrogator.assertCpuState {
            programCounter(0x1234)
        }
    }
}