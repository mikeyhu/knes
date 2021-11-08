package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.AddWithCarryCheck
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class AddWithCarryOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<AddWithCarryCheck> {
            return Stream.of(
                AddWithCarryCheck(0x0fu, 0x01u, false, 0x10u, false, false, false, false),
                AddWithCarryCheck(0x7fu, 0x0u, false, 0x7fu, false, false, false, false),
                AddWithCarryCheck(0x7fu, 0x0u, true, 0x80u, true, true, false, false),
                AddWithCarryCheck(0x80u, 0x0u, false, 0x80u, true, false, false, false),
                AddWithCarryCheck(0x0u, 0x0u, false, 0x0u, false, false, false, true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Immediate`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_I, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }


    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC ZeroPage`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_Z, 0x03u, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC ZeroPage X`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_ZX, 0x02u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x02u), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Absolute`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_AB, 0x03u, 0x0u, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Absolute X`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x2u), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Absolute Y`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, yReg = 0x2u), memory)

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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Indirect Indexed`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_IIY, 0xf0u, size = 0xffff))

        memory[0xf0] = 0xf0u
        memory[0xf1] = 0xeeu
        val yReg: UByte = 0x5u
        memory[0xeef5] = data.memory

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, yReg = yReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ADC_IIY)
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
                memoryRead(0xeef5, data.memory)
            }
        }

        interrogator.assertCpuState {
            programCounter(2)
            aReg(data.expected)
            isNegativeFlag(data.negativeFlag)
            isOverflowFlag(data.overflowFlag)
            isCarryFlag(data.carryFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ADC Indexed Indirect`(data: AddWithCarryCheck) {
        val memory = BasicMemory(setupMemory(ADC_IIX, 0xf0u, size = 0xffff))

        memory[0xf5] = 0xf0u
        memory[0xf6] = 0xeeu
        val xReg: UByte = 0x5u
        memory[0xeef0] = data.memory

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = xReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ADC_IIX)
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
                memoryRead(0xeef0, data.memory)
            }
        }

        interrogator.assertCpuState {
            programCounter(2)
            aReg(data.expected)
            isNegativeFlag(data.negativeFlag)
            isOverflowFlag(data.overflowFlag)
            isCarryFlag(data.carryFlag)
            isZeroFlag(data.zeroFlag)
        }
    }
}