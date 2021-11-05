package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.ShiftCheck
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class RotateRightOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x3u, 0x1u, false, true, false),
                ShiftCheck(0x1u, 0x0u, false, true, true),
                ShiftCheck(0x0u, 0x80u, true, false, false, carryIn = true),
                ShiftCheck(0x1u, 0x80u, true, true, false, carryIn = true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROR Accumulator`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROR_NONE))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.input, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROR_NONE)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            aReg(data.output)
            isCarryFlag(data.carryFlag)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROR ZeroPage`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROR_Z, 0x03u, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROR_Z)
            }
            cycle {
                memoryRead(1, 0x03u)
            }
            cycle {
                memoryRead(0x03, data.input)
            }
            cycle {}
            cycle {
                memoryWrite(0x03, data.output)
            }
        }

        interrogator.assertCpuState {
            programCounter(2)
            isCarryFlag(data.carryFlag)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROR ZeroPage X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROR_ZX, 0x03u, NOP, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x02u, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROR_ZX)
            }
            cycle {
                memoryRead(1, 0x03u)
            }
            cycle {}
            cycle {
                memoryRead(0x05, data.input)
            }
            cycle {}
            cycle {
                memoryWrite(0x05, data.output)
            }
        }

        interrogator.assertCpuState {
            programCounter(2)
            isCarryFlag(data.carryFlag)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROR Absolute`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROR_AB, 0x03u, 0x0u, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROR_AB)
            }
            cycle {
                memoryRead(1, 0x03u)
            }
            cycle {
                memoryRead(2, 0x00u)
            }
            cycle {
                memoryRead(0x03, data.input)
            }
            cycle {}
            cycle {
                memoryWrite(0x03, data.output)
            }
        }

        interrogator.assertCpuState {
            programCounter(3)
            isCarryFlag(data.carryFlag)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROR Absolute X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROR_ABX, 0x03u, 0x0u, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x2u, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROR_ABX)
            }
            cycle {
                memoryRead(1, 0x03u)
            }
            cycle {
                memoryRead(2, 0x00u)
            }
            cycle {}
            cycle {
                memoryRead(0x05, data.input)
            }
            cycle {}
            cycle {
                memoryWrite(0x05, data.output)
            }
        }

        interrogator.assertCpuState {
            programCounter(3)
            isCarryFlag(data.carryFlag)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }
}