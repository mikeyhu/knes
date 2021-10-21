package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource


@ExperimentalUnsignedTypes
class LoadOperationsTest {

    @Nested
    inner class LDA : ParameterizedTestData() {
        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Immediate`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_I, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Absolute`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_AB)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Absolute X`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_ABX, 0x04u, 0x00u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0x01u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_ABX)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Absolute X crossing page boundary`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_ABX, 0x10u, 0x00u, NOP))

            memory[0x10f] = data.input

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0xffu
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_ABX)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(0x10f, data.input)
                }
                cycle {

                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA ZeroPage`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_Z, 0x03u, NOP, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_Z)
                }
                cycle {
                    memoryRead(1, 0x3u)
                }
                cycle {
                    memoryRead(3, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }

    @Nested
    inner class LDX : ParameterizedTestData() {

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDX Immediate`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_I, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                xReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDX Absolute`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_AB)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                xReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }

    @Nested
    inner class LDY : ParameterizedTestData() {

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDY Immediate`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_I, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                yReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDY Absolute`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(CpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_AB)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                yReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }
}