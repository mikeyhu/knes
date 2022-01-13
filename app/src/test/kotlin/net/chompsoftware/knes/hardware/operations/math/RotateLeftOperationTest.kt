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

class RotateLeftOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x81u, 0x2u, false, true, false),
                ShiftCheck(0x81u, 0x3u, false, true, false, carryIn = true),
                ShiftCheck(0x00u, 0x1u, false, false, false, carryIn = true),
                ShiftCheck(0x80u, 0x0u, false, true, true),
                ShiftCheck(0x40u, 0x80u, true, false, false),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ROL Accumulator`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROL_NONE))

        val interrogator =
            HardwareInterrogator(randomisedCpuState(aReg = data.input, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROL_NONE)
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
    fun `ROL ZeroPage`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROL_Z, 0x03u, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROL_Z)
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
    fun `ROL ZeroPage X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROL_ZX, 0x03u, NOP, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x02u, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROL_ZX)
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
    fun `ROL Absolute`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROL_AB, 0x03u, 0x0u, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROL_AB)
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
    fun `ROL Absolute X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ROL_ABX, 0x03u, 0x0u, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x2u, isCarryFlag = data.carryIn), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ROL_ABX)
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