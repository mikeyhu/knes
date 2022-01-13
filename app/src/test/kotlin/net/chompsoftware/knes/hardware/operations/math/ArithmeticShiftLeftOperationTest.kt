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

class ArithmeticShiftLeftOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0xf2u, 0xe4u, true, true, false),
                ShiftCheck(0x0u, 0x0u, false, false, true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ASL Accumulator`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ASL_NONE))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASL_NONE)
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
    fun `ASL ZeroPage`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ASL_Z, 0x03u, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASL_Z)
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
    fun `ASL ZeroPage X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ASL_ZX, 0x03u, NOP, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x02u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASL_ZX)
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
    fun `ASL Absolute`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ASL_AB, 0x03u, 0x0u, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASL_AB)
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
    fun `ASL Absolute X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(ASL_ABX, 0x03u, 0x0u, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x2u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASL_ABX)
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