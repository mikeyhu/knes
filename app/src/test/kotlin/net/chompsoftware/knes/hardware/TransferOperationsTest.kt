package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalUnsignedTypes
class TransferOperationsTest : ParameterizedTestData() {

    @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
    @MethodSource("checkNegativeZeroFlags")
    fun `TAX - Transfer Accumulator to X`(data: InputWithNegativeZeroCheck) {
        val memory = BasicMemory(setupMemory(TAX, NOP))

        val interrogator = HardwareInterrogator(CpuState(aReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TAX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(data.input)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
    @MethodSource("checkNegativeZeroFlags")
    fun `TXA - Transfer X to Accumulator`(data: InputWithNegativeZeroCheck) {
        val memory = BasicMemory(setupMemory(TXA, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TXA)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            aReg(data.input)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @Test
    fun `TXS - Transfer X to Stack Register`() {
        val memory = BasicMemory(setupMemory(TXS, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = 0x10u, stackReg = 0x0u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TXS)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            stackReg(0x10u)
        }
    }

    @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
    @MethodSource("checkNegativeZeroFlags")
    fun `TYA - Transfer Y to Accumulator`(data: InputWithNegativeZeroCheck) {
        val memory = BasicMemory(setupMemory(TYA, NOP))

        val interrogator = HardwareInterrogator(CpuState(yReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TYA)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            aReg(data.input)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }
}
