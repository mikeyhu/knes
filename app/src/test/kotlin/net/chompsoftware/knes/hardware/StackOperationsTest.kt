package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StackOperationsTest {

    @Nested
    inner class PHA {
        @Test
        fun `PHA - Push Accumulator to the stack`() {
            val memory = BasicMemory(setupMemory(PHA, NOP))

            val accumulator: UByte = 0x11u
            val stackRegister: UByte = 0xffu
            val expectedStackPosition = 0x1ff
            val expectedStackRegister: UByte = 0xfeu

            val interrogator = HardwareInterrogator(CpuState(aReg = accumulator, stackReg = stackRegister), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, PHA)
                }
                cycle {}
                cycle {
                    memoryWrite(expectedStackPosition, accumulator)
                }
            }

            interrogator.assertCpuState {
                programCounter(1)
                stackReg(expectedStackRegister)
            }
        }

        @Test
        fun `PHA - Should underflow when necessary`() {
            val memory = BasicMemory(setupMemory(PHA, NOP))

            val accumulator: UByte = 0x11u
            val stackRegister: UByte = 0x00u
            val expectedStackPosition = 0x100
            val expectedStackRegister: UByte = 0xffu

            val interrogator = HardwareInterrogator(CpuState(aReg = accumulator, stackReg = stackRegister), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, PHA)
                }
                cycle {}
                cycle {
                    memoryWrite(expectedStackPosition, accumulator)
                }
            }

            interrogator.assertCpuState {
                programCounter(1)
                stackReg(expectedStackRegister)
            }
        }
    }
}