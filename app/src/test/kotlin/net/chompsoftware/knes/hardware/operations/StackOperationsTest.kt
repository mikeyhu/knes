package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.InputWithNegativeZeroCheck
import net.chompsoftware.knes.hardware.utilities.ParameterizedTestData
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalUnsignedTypes
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

        @ParameterizedTest
        @CsvSource(
            "0x00u, false, false, false, false, false, false, false",
            "0x01u, false, false, false, false, false, false, true",
            "0x02u, false, false, false, false, false, true,  false",
            "0x04u, false, false, false, false, true,  false, false",
            "0x08u, false, false, false, true,  false, false, false",
            "0x10u, false, false, true,  false, false, false, false",
            "0x40u, false, true,  false, false, false, false, false",
            "0x80u, true,  false, false, false, false, false, false",
            "0xffu, true,  true,  true,  true,  true,  true,  true"
        )
        fun `PLP - Pull Processor status from the stack`(
            value: String,
            negativeFlag: Boolean,
            overflowFlag: Boolean,
            breakCommandFlag: Boolean,
            decimalFlag: Boolean,
            interruptDisabledFlag: Boolean,
            zeroFlag: Boolean,
            carryFlag: Boolean
        ) {
            val memory = BasicMemory(setupMemory(PLP, NOP))

            val stackRegister: UByte = 0xfeu
            val expectedStackPosition = 0x1ff
            val expectedStackRegister: UByte = 0xffu

            memory[expectedStackPosition] = value.toHexUByte()

            val interrogator = HardwareInterrogator(CpuState(stackReg = stackRegister), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, PLP)
                }
                cycle {}
                cycle {}
                cycle {
                    memoryRead(expectedStackPosition, value.toHexUByte())
                }
            }

            interrogator.assertCpuState {
                programCounter(1)
                stackReg(expectedStackRegister)
                isNegativeFlag(negativeFlag)
                isCarryFlag(carryFlag)
                isZeroFlag(zeroFlag)
                isDecimalFlag(decimalFlag)
                isBreakCommandFlag(breakCommandFlag)
                isOverflowFlag(overflowFlag)
                isInterruptDisabledFlag(interruptDisabledFlag)
            }
        }

        @ParameterizedTest
        @CsvSource(
            "0x30u, false, false, false, false, false, false, false",
            "0x31u, false, false, false, false, false, false, true",
            "0x32u, false, false, false, false, false, true,  false",
            "0x34u, false, false, false, false, true,  false, false",
            "0x38u, false, false, false, true,  false, false, false",
            "0x30u, false, false, true,  false, false, false, false",
            "0x70u, false, true,  false, false, false, false, false",
            "0xb0u, true,  false, false, false, false, false, false",
            "0xffu, true,  true,  true,  true,  true,  true,  true"
        )
        // These always have bit 5 and break (bit 4) set
        fun `PHP - Push Processor status to the stack`(
            value: String,
            negativeFlag: Boolean,
            overflowFlag: Boolean,
            breakCommandFlag: Boolean,
            decimalFlag: Boolean,
            interruptDisabledFlag: Boolean,
            zeroFlag: Boolean,
            carryFlag: Boolean
        ) {
            val memory = BasicMemory(setupMemory(PHP, NOP))

            val stackRegister: UByte = 0xffu
            val expectedStackPosition = 0x1ff
            val expectedStackRegister: UByte = 0xfeu

            val interrogator = HardwareInterrogator(
                CpuState(
                    stackReg = stackRegister,
                    isNegativeFlag = negativeFlag,
                    isCarryFlag = carryFlag,
                    isZeroFlag = zeroFlag,
                    isDecimalFlag = decimalFlag,
                    isBreakCommandFlag = breakCommandFlag,
                    isOverflowFlag = overflowFlag,
                    isInterruptDisabledFlag = interruptDisabledFlag
                ), memory
            )

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, PHP)
                }
                cycle {}
                cycle {
                    memoryWrite(expectedStackPosition, value.toHexUByte())
                }
            }

            interrogator.assertCpuState {
                programCounter(1)
                stackReg(expectedStackRegister)
            }
        }
    }
}