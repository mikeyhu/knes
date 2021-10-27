package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalUnsignedTypes
class ComparisonOperationsTest {
    @Nested
    inner class CMP : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CMP Immediate`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CMP_I, data.input, NOP))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing), memory)

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

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing), memory)

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

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing, xReg=0x01u), memory)

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

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing, yReg=0x01u), memory)

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

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing, yReg=0x01u), memory)

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
    }

    @Nested
    inner class CPX : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPX Immediate - Compare X`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPX_I, data.input, NOP))

            val interrogator = HardwareInterrogator(CpuState(xReg = data.existing), memory)

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
    }

    @Nested
    inner class CPY : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkComparisonNegativeZeroCarryFlags")
        fun `CPY Immediate - Compare Y`(data: ComparisonWithNegativeZeroCarryCheck) {
            val memory = BasicMemory(setupMemory(CPY_I, data.input, NOP))

            val interrogator = HardwareInterrogator(CpuState(yReg = data.existing), memory)

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
    }
}