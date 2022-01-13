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

class IncrementOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x00u, 0x01u, negativeFlag = false, zeroFlag = false),
                ShiftCheck(0xffu, 0x00u, negativeFlag = false, zeroFlag = true),
                ShiftCheck(0x7fu, 0x80u, negativeFlag = true, zeroFlag = false),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INC ZeroPage`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INC_Z, 0x03u, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INC_Z)
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
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INC ZeroPage X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INC_ZX, 0x03u, NOP, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x02u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INC_ZX)
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
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INC Absolute`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INC_AB, 0x03u, 0x00u, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INC_AB)
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
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INC Absolute X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INC_ABX, 0x03u, 0x00u, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = 0x2u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INC_ABX)
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
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INX - Increment X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INX, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(xReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(data.output)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `INY - Increment Y`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(INY, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(yReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INY)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            yReg(data.output)
            isNegativeFlag(data.negativeFlag)
            isZeroFlag(data.zeroFlag)
        }
    }
}