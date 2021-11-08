package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.RegisterMemoryExpectedCheck
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class OrWithAccumulatorOperationTest {

    companion object {
        @JvmStatic
        fun checkFlags(): Stream<RegisterMemoryExpectedCheck> {
            return Stream.of(
                RegisterMemoryExpectedCheck(0x0fu, 0xf0u, 0xffu, true, false),
                RegisterMemoryExpectedCheck(0xf0u, 0xffu, 0xffu, true, false),
                RegisterMemoryExpectedCheck(0x00u, 0x00u, 0x00u, false, true),
                RegisterMemoryExpectedCheck(0x0fu, 0x10u, 0x1fu, false, false),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ORA Immediate`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_I, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

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
    @MethodSource("checkFlags")
    fun `ORA ZeroPage`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_Z, 0x03u, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

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
    @MethodSource("checkFlags")
    fun `ORA ZeroPage X`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_ZX, 0x02u, NOP, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, xReg = 0x02u), memory)

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
    @MethodSource("checkFlags")
    fun `ORA Absolute`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_AB, 0x03u, 0x0u, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

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
    @MethodSource("checkFlags")
    fun `ORA Absolute X`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_ABX, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, xReg = 0x2u), memory)

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
    @MethodSource("checkFlags")
    fun `ORA Absolute Y`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_ABY, 0x03u, 0x0u, NOP, NOP, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, yReg = 0x2u), memory)

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

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ORA Indirect Indexed`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_IIY, 0xf0u, size = 0xffff))

        memory[0xf0] = 0xf0u
        memory[0xf1] = 0xeeu
        val yReg: UByte = 0x5u
        memory[0xeef5] = data.memory

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, yReg = yReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ORA_IIY)
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
            isZeroFlag(data.zeroFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("checkFlags")
    fun `ORA Indexed Indirect`(data: RegisterMemoryExpectedCheck) {
        val memory = BasicMemory(setupMemory(ORA_IIX, 0xf0u, size = 0xffff))

        memory[0xf5] = 0xf0u
        memory[0xf6] = 0xeeu
        val xReg: UByte = 0x5u
        memory[0xeef0] = data.memory

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg, xReg = xReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ORA_IIX)
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
            isZeroFlag(data.zeroFlag)
        }
    }
}