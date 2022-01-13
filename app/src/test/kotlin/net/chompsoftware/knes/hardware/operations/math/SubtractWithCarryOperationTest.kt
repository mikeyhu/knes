package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SubtractWithCarryOperationTest {

    data class SubtractWithCarryCheck(
        val aReg: UByte,
        val memory: UByte,
        val carry: Boolean,
        val expected: UByte,
        val negativeFlag: Boolean,
        val overflowFlag: Boolean,
        val carryFlag: Boolean,
        val zeroFlag: Boolean
    )

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<SubtractWithCarryCheck> {
            return Stream.of(
                SubtractWithCarryCheck(0x1fu, 0xfu, true, 0x10u, false, false, true, false),
                SubtractWithCarryCheck(0x0u, 0xffu, false, 0x0u, false, false, false, true),
                SubtractWithCarryCheck(0x7fu, 0xffu, true, 0x80u, true, true, false, false),
                SubtractWithCarryCheck(0xffu, 0x0ffu, true, 0x00u, false, false, true, true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `SBC Immediate`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_I, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_I)
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
    fun `SBC ZeroPage`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_Z, 0x03u, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_Z)
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
    fun `SBC ZeroPage X`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_ZX, 0x02u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x02u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_ZX)
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
    fun `SBC Absolute`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_AB, 0x03u, 0x0u, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_AB)
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
    fun `SBC Absolute X`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = 0x2u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_ABX)
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
    fun `SBC Absolute Y`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, yReg = 0x2u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_ABY)
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
    fun `SBC Indirect Indexed`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_IIY, 0xf0u, size = 0xffff))

        memory[0xf0] = 0xf0u
        memory[0xf1] = 0xeeu
        val yReg: UByte = 0x5u
        memory[0xeef5] = data.memory

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, yReg = yReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_IIY)
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
    fun `SBC Indexed Indirect`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_IIX, 0xf0u, size = 0xffff))

        memory[0xf5] = 0xf0u
        memory[0xf6] = 0xeeu
        val xReg: UByte = 0x5u
        memory[0xeef0] = data.memory

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry, xReg = xReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_IIX)
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

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `SBC Immediate - unofficial 0xebu`(data: SubtractWithCarryCheck) {
        val memory = BasicMemory(setupMemory(SBC_I_UN_EB, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, isCarryFlag = data.carry), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SBC_I_UN_EB)
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
}