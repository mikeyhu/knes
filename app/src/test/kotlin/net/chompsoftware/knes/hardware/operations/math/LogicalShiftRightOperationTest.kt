package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.ShiftCheck
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class LogicalShiftRightOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<ShiftCheck> {
            return Stream.of(
                ShiftCheck(0x81u, 0x40u, false, true, false),
                ShiftCheck(0x80u, 0x40u, false, false, false),
                ShiftCheck(0xf2u, 0x79u, false, false, false),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `LSR Accumulator`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(LSR_NONE))

        val interrogator = HardwareInterrogator(CpuState(aReg = data.input), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LSR_NONE)
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
    fun `LSR ZeroPage`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(LSR_Z, 0x03u, NOP, data.input))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LSR_Z)
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
    fun `LSR ZeroPage X`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(LSR_ZX, 0x03u, NOP, NOP, NOP, data.input))

        val interrogator = HardwareInterrogator(CpuState(xReg = 0x02u), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LSR_ZX)
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
    fun `LSR Absolute`(data: ShiftCheck) {
        val memory = BasicMemory(setupMemory(LSR_AB, 0x03u, 0x0u, data.input))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LSR_AB)
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
}