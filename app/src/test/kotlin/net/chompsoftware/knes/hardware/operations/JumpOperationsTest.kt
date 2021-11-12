package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class JumpOperationsTest {
    @Test
    fun `JMP - Jump to absolute position`() {
        val memory = BasicMemory(setupMemory(JMP_AB, 0x34u, 0x12u))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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
    fun `JMP - Jump to indirect position wrap-around`() {
        /* http://6502.org/tutorials/6502opcodes.html
        Note that there is no carry associated with the indirect jump so:
        AN INDIRECT JUMP MUST NEVER USE A
        VECTOR BEGINNING ON THE LAST BYTE
        OF A PAGE
        For example if address $3000 contains $40, $30FF contains $80, and $3100 contains $50, the result of JMP ($30FF) will be a transfer of control to $4080 rather than $5080 as you intended i.e. the 6502 took the low byte of the address from $30FF and the high byte from $3000.
         */
        val memory = BasicMemory(setupMemory(JMP_IN, 0xffu, 0x30u, NOP))

        memory[0x3000] = 0x40u
        memory[0x30ff] = 0x80u
        memory[0x3100] = 0x50u

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, JMP_IN)
            }
            cycle {
                memoryRead(1, 0xffu)
            }
            cycle {
                memoryRead(2, 0x30u)
            }
            cycle {
                memoryRead(0x30ff, 0x80u)
            }
            cycle {
                memoryRead(0x3000, 0x40u)
            }
        }

        interrogator.assertCpuState {
            programCounter(0x4080)
        }
    }

    @Test
    fun `JSR - Jump to subroutine`() {
        val memory = BasicMemory(setupMemory(JSR_AB, 0x34u, 0x12u))

        val interrogator = HardwareInterrogator(randomisedCpuState(stackReg = 0xffu), memory)

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

    @Test
    fun `RTS - Return from subroutine`() {
        val memory = BasicMemory(setupMemory(RTS, NOP))

        memory[0x1fe] = 0x34u
        memory[0x1ff] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xfdu
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, RTS)
            }
            cycle {}
            cycle {}
            cycle {
                memoryRead(0x1fe, 0x34u)
            }
            cycle {
                memoryRead(0x1ff, 0x12u)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(0x1235)
            stackReg(0xffu)
        }
    }

    @Test
    fun `RTS - Return from subroutine (stack wrap)`() {
        val memory = BasicMemory(setupMemory(RTS, NOP))

        memory[0x1ff] = 0x34u
        memory[0x100] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xfeu
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, RTS)
            }
            cycle {}
            cycle {}
            cycle {
                memoryRead(0x1ff, 0x34u)
            }
            cycle {
                memoryRead(0x100, 0x12u)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(0x1235)
            stackReg(0x00u)
        }
    }
}