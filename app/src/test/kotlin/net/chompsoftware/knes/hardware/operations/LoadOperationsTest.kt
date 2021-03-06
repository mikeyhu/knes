package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.InputWithNegativeZeroCheck
import net.chompsoftware.knes.hardware.utilities.ParameterizedTestData
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class LoadOperationsTest {

    @Nested
    inner class LDA : ParameterizedTestData() {
        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Immediate`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_I, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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
        fun `LDA Absolute Y`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_ABY, 0x04u, 0x00u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    yReg = 0x01u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_ABY)
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
        fun `LDA ZeroPage`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_Z, 0x03u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA ZeroPage plus X`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_ZX, 0x03u, NOP, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0x2u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_ZX)
                }
                cycle {
                    memoryRead(1, 0x3u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
                cycle {

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
        fun `LDA Indirect Indexed`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_IIY, 0xf0u, size = 0xffff))

            memory[0xf0] = 0xf0u
            memory[0xf1] = 0xeeu
            val yReg: UByte = 0x5u
            memory[0xeef5] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_IIY)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {
                    memoryRead(0xf0, 0xf0u)
                }
                cycle {
                    memoryRead(0xf1, 0xeeu)
                }
                cycle {
                    memoryRead(0xeef5, data.input)
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
        fun `LDA Indirect Indexed with page boundary crossing`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_IIY, 0xf0u, size = 0xffff))

            memory[0xf0] = 0xf0u
            memory[0xf1] = 0xeeu
            val yReg: UByte = 0x15u
            memory[0xef05] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_IIY)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {
                    memoryRead(0xf0, 0xf0u)
                }
                cycle {
                    memoryRead(0xf1, 0xeeu)
                }
                cycle {
                    memoryRead(0xef05, data.input)
                }
                cycle {}
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
        fun `LDA Indirect Indexed (wrapping)`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(0xeeu, NOP, LDA_IIY, 0xffu, size = 0xffff))

            memory[0xff] = 0xf0u
            val yReg: UByte = 0x15u
            memory[0xef05] = data.input

            val interrogator = HardwareInterrogator(
                randomisedCpuState(
                    programCounter = 2,
                    yReg = yReg
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(2, LDA_IIY)
                }
                cycle {
                    memoryRead(3, 0xffu)
                }
                cycle {
                    memoryRead(0xff, 0xf0u)
                }
                cycle {
                    memoryRead(0x00, 0xeeu)
                }
                cycle {
                    memoryRead(0xef05, data.input)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(4)
                aReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDA Indexed Indirect`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDA_IIX, 0xf0u, size = 0xffff))

            memory[0xf5] = 0xf0u
            memory[0xf6] = 0xeeu
            val xReg: UByte = 0x5u
            memory[0xeef0] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDA_IIX)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {}
                cycle {
                    memoryRead(0xf5, 0xf0u)
                }
                cycle {
                    memoryRead(0xf6, 0xeeu)
                }
                cycle {
                    memoryRead(0xeef0, data.input)
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
        fun `LDA Indexed Indirect (wrapping)`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(0xeeu, NOP, LDA_IIX, 0xfeu, size = 0xffff))

            memory[0xff] = 0xf0u
            val xReg: UByte = 1u
            memory[0xeef0] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 2, xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(2, LDA_IIX)
                }
                cycle {
                    memoryRead(3, 0xfeu)
                }
                cycle {}
                cycle {
                    memoryRead(0xff, 0xf0u)
                }
                cycle {
                    memoryRead(0x00, 0xeeu)
                }
                cycle {
                    memoryRead(0xeef0, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(4)
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

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDX Absolute Y offset`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_ABY, 0x02u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_ABY)
                }
                cycle {
                    memoryRead(1, 0x2u)
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

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDX ZeroPage`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_Z, 0x03u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_Z)
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
                xReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDX ZeroPage plus Y`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_ZY, 0x03u, NOP, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    yReg = 0x2u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_ZY)
                }
                cycle {
                    memoryRead(1, 0x3u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
                cycle {

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
        fun `LDX ZeroPage plus Y should wrap`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDX_ZY, 0x04u, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    yReg = 0xffu
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDX_ZY)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(3, data.input)
                }
                cycle {

                }
            }

            interrogator.assertCpuState {
                programCounter(2)
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

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

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

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDY Absolute X`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_ABX, 0x04u, 0x00u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0x01u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_ABX)
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
                yReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDY ZeroPage`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_Z, 0x03u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_Z)
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
                yReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
        @MethodSource("checkNegativeZeroFlags")
        fun `LDY ZeroPage plus X`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_ZX, 0x03u, NOP, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0x2u
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_ZX)
                }
                cycle {
                    memoryRead(1, 0x3u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
                cycle {

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
        fun `LDY ZeroPage plus X should wrap`(data: InputWithNegativeZeroCheck) {
            val memory = BasicMemory(setupMemory(LDY_ZX, 0x04u, NOP, data.input))

            val interrogator = HardwareInterrogator(
                CpuState(
                    xReg = 0xffu
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, LDY_ZX)
                }
                cycle {
                    memoryRead(1, 0x4u)
                }
                cycle {
                    memoryRead(3, data.input)
                }
                cycle {

                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                yReg(data.input)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }
}