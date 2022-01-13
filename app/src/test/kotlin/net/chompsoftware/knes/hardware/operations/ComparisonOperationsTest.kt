package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.ComparisonWithNegativeZeroCarryCheck
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.ParameterizedTestData
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ComparisonOperationsTest {
    @Nested
    inner class CMP : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Immediate`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_I, data.input, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Absolute`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_AB)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Absolute X`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_ABX, 0x04u, 0x00u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, xReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_ABX)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Absolute Y`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_ABY, 0x04u, 0x00u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, yReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_ABY)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Absolute Y crossing page boundary`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_ABY, 0xffu, 0x00u))
            memory[0x100] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, yReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_ABY)
                }
                cycle {
                    memoryRead(1, 0xffu)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(0x100, data.input)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP ZeroPage`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_Z, 0x04u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_Z)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP ZeroPage X`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_ZX, 0x04u, NOP, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, xReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_ZX)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(5, data.input)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Indirect Indexed`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_IIY, 0xf0u, size = 0xffff))

            memory[0xf0] = 0xf0u
            memory[0xf1] = 0xeeu
            val yReg: UByte = 0x5u
            memory[0xeef5] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_IIY)
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
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Indexed Indirect`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_IIX, 0xf0u, size = 0xffff))

            memory[0xf5] = 0xf0u
            memory[0xf6] = 0xeeu
            val xReg: UByte = 0x5u
            memory[0xeef0] = data.input

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.existing, xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CMP_IIX)
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
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }
    }

    @Nested
    inner class CPX : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPX Immediate - Compare X`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPX_I, data.input, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPX_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPX Absolute`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPX_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPX_AB)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPX ZeroPage`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPX_Z, 0x04u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPX_Z)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }
    }

    @Nested
    inner class CPY : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPY Immediate - Compare Y`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPY_I, data.input, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPY_I)
                }
                cycle {
                    memoryRead(1, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPY Absolute`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPY_AB, 0x04u, 0x00u, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPY_AB)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPY ZeroPage`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPY_Z, 0x04u, NOP, NOP, data.input))

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = data.existing), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, CPY_Z)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {
                    memoryRead(4, data.input)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isCarryFlag(data.carryFlag)
            }
        }
    }
}