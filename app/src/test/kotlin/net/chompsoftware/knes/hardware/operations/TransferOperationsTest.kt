package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.InputWithNegativeZeroCheck
import net.chompsoftware.knes.hardware.utilities.ParameterizedTestData
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
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

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.input), memory)

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
    fun `TAY - Transfer Accumulator to Y`(data: InputWithNegativeZeroCheck) {
        val memory = BasicMemory(setupMemory(TAY, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TAY)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            yReg(data.input)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest(name = NEGATIVE_ZERO_CHECK)
    @MethodSource("checkNegativeZeroFlags")
    fun `TSX - Transfer Stack Register to X`(data: InputWithNegativeZeroCheck) {
        val memory = BasicMemory(setupMemory(TSX, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x0u, stackReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TSX)
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

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = data.input), memory)

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

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x10u, stackReg = 0x0u), memory)

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

        val interrogator = HardwareInterrogator(randomisedCpuState(yReg = data.input), memory)

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
