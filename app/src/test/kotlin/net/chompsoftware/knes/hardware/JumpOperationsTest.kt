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

    @Test
    fun `JMP - Jump to indirect position`() {
        val memory = BasicMemory(setupMemory(JMP_IN, 0x04u, 0x00u, NOP, 0x34u, 0x12u))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, JMP_IN)
            }
            cycle {
                memoryRead(1, 0x04u)
            }
            cycle {
                memoryRead(2, 0x00u)
            }
            cycle {
                memoryRead(4, 0x34u)
            }
            cycle {
                memoryRead(5, 0x12u)
            }
        }

        interrogator.assertCpuState {
            programCounter(0x1234)
        }
    }

    @Test
    fun `JSR - Jump to subroutine`() {
        val memory = BasicMemory(setupMemory(JSR_AB, 0x34u, 0x12u))

        val interrogator = HardwareInterrogator(CpuState(stackReg = 0xffu), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, JSR_AB)
            }
            cycle {
                memoryRead(1, 0x34u)
            }
            cycle {
                memoryRead(2, 0x12u)
            }
            cycle {

            }
            cycle {
                memoryWrite(0x1ff, 0x00u)
            }
            cycle {
                memoryWrite(0x1fe, 0x02u)
            }
        }

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xfdu)
        }
    }
}