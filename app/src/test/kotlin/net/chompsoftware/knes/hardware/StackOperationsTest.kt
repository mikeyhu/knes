package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class StackOperationsTest {

    @Nested
    inner class PHA : ParameterizedTestData() {
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

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `PLA - Pull Accumulator from the stack`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(PLA, NOP))

            val stackRegister: UByte = 0xfeu
            val expectedStackPosition = 0x1ff
            val expectedStackRegister: UByte = 0xffu

            memory[expectedStackPosition] = data.input

            val interrogator = HardwareInterrogator(CpuState(stackReg = stackRegister), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, PLA)
                }
                cycle {}
                cycle {}
                cycle {
                    memoryRead(expectedStackPosition, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(1)
                aReg(data.input)
                stackReg(expectedStackRegister)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }
}