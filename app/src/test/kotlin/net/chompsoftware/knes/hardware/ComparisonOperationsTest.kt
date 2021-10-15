package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
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

            val interrogator = HardwareInterrogator(CpuState(aReg = data.existing.toUInt()), memory)

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
    }
}