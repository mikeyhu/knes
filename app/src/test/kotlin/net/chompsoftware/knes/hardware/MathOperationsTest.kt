package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource


class MathOperationsTest : ParameterizedTestData() {

    @Nested
    inner class ADC : ParameterizedTestData() {


        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC Immediate`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_I, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_I)
                }
                cycle {
                    memoryRead(1, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isOverflowFlag(data.overflowFlag)
                isCarryFlag(data.carryFlag)
            }
        }
    }

    @ParameterizedTest()
    @CsvSource(
        "0x10u, 0x0fu, false, false",
        "0x01u, 0x00u, false, true",
        "0x81u, 0x80u, true, false",
        "0x0u, 0xffu, true, false",
    )
    fun `DEX - Decrement X`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(DEX, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, DEX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(expected.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }

    @ParameterizedTest()
    @CsvSource(
        "0x10u, 0x0fu, false, false",
        "0x01u, 0x00u, false, true",
        "0x81u, 0x80u, true, false",
        "0x0u, 0xffu, true, false",
    )
    fun `DEY - Decrement Y`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(DEY, NOP))

        val interrogator = HardwareInterrogator(CpuState(yReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, DEY)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            yReg(expected.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }

    @ParameterizedTest()
    @CsvSource(
        "0x00u, 0x01u, false, false",
        "0xffu, 0x00u, false, true",
        "0x7fu, 0x80u, true,  false",
    )
    fun `INX - Increment X`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(INX, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(expected.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }
}