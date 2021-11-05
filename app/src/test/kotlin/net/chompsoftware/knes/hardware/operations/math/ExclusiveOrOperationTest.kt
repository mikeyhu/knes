package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.RegisterMemoryExpectedCheck
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class ExclusiveOrOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0x0fu, 0xffu, 0xf0u, true, false),
                RegisterMemoryExpectedCheck(0xf0u, 0xffu, 0x0fu, false, false),
                RegisterMemoryExpectedCheck(0xffu, 0xffu, 0x00u, false, true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
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
    @MethodSource("checkFlags")
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
    @MethodSource("checkFlags")
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
    @MethodSource("checkFlags")
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
    @MethodSource("checkFlags")
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
    @MethodSource("checkFlags")
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