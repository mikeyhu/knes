package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.BIT_AB
import net.chompsoftware.knes.hardware.instructions.BIT_Z
import net.chompsoftware.knes.hardware.instructions.NOP
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.RegisterMemoryExpectedCheck
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class BitWithAccumulatorOperationTest {

    companion object {
        @JvmStatic
        fun checkBitFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0xffu, 0xf0u, 0xffu, true, false, true),
                RegisterMemoryExpectedCheck(0xffu, 0xffu, 0xffu, true, false, true),
                RegisterMemoryExpectedCheck(0x01u, 0x01u, 0x01u, false, false, false),
                RegisterMemoryExpectedCheck(0x10u, 0x01u, 0x10u, false, true, false),
                RegisterMemoryExpectedCheck(0x01u, 0xffu, 0x01u, true, false, true),
                RegisterMemoryExpectedCheck(0x01u, 0x10u, 0x01u, false, true, false),
            )
        }
    }

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