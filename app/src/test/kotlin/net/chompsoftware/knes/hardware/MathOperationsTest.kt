package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalUnsignedTypes
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


        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC ZeroPage`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_Z, 0x03u, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(3, data.memory)
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

        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC ZeroPage X`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_ZX, 0x02u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_ZX)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {
                    memoryRead(4, data.memory)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isOverflowFlag(data.overflowFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC Absolute`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_AB, 0x03u, 0x0u, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_AB)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(3, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isOverflowFlag(data.overflowFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC Absolute X`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_ABX)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isOverflowFlag(data.overflowFlag)
                isCarryFlag(data.carryFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkAddWithCarryFlags")
        fun `ADC Absolute Y`(data: AddWithCarryCheck) {
            val memory = BasicMemory(setupMemory(ADC_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, isCarryFlag = data.carry, yReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ADC_ABY)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
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

    @Nested
    inner class EOR : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")

        fun `EOR Immediate - Exclusive OR`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_I, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_I)
                }
                cycle {
                    memoryRead(1, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)

            }
        }


        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")
        fun `EOR ZeroPage`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_Z, 0x03u, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(3, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")
        fun `EOR ZeroPage X`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_ZX, 0x02u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, xReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_ZX)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {
                    memoryRead(4, data.memory)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")
        fun `EOR Absolute`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_AB, 0x03u, 0x0u, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_AB)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(3, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")
        fun `EOR Absolute X`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, xReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_ABX)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkExclusiveOrFlags")
        fun `EOR Absolute Y`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(EOR_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, yReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, EOR_ABY)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
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

    @ParameterizedTest()
    @CsvSource(
        "0x00u, 0x01u, false, false",
        "0xffu, 0x00u, false, true",
        "0x7fu, 0x80u, true,  false",
    )
    fun `INY - Increment Y`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(INY, NOP))

        val interrogator = HardwareInterrogator(CpuState(yReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INY)
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

    @Nested
    inner class ORA : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA Immediate`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_I, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_I)
                }
                cycle {
                    memoryRead(1, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA ZeroPage`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_Z, 0x03u, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(3, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA ZeroPage X`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_ZX, 0x02u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, xReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_ZX)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {
                    memoryRead(4, data.memory)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA Absolute`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_AB, 0x03u, 0x0u, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_AB)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(3, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA Absolute X`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, xReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_ABX)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkOrFlags")
        fun `ORA Absolute Y`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(ORA_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg, yReg = 0x2u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, ORA_ABY)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x0u)
                }
                cycle {
                    memoryRead(5, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
            }
        }
    }

    @Nested
    inner class BIT : ParameterizedTestData() {
        @ParameterizedTest
        @MethodSource("checkBitFlags")
        fun `BIT ZeroPage`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(BIT_Z, 0x03u, NOP, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BIT_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(0x03, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isOverflowFlag(data.overflowFlag)
            }
        }

        @ParameterizedTest
        @MethodSource("checkBitFlags")
        fun `BIT Absolute`(data: RegisterMemoryExpectedCheck) {
            val memory = BasicMemory(setupMemory(BIT_AB, 0x03u, 0x00u, data.memory))

            val interrogator = HardwareInterrogator(CpuState(aReg = data.aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BIT_AB)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryRead(2, 0x00u)
                }
                cycle {
                    memoryRead(0x03, data.memory)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
                aReg(data.expected)
                isNegativeFlag(data.negativeFlag)
                isZeroFlag(data.zeroFlag)
                isOverflowFlag(data.overflowFlag)
            }
        }
    }
}