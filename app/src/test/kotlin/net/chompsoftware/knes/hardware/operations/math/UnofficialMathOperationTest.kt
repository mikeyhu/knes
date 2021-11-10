package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.ANC_I_UN_0B
import net.chompsoftware.knes.hardware.instructions.ANC_I_UN_2B
import net.chompsoftware.knes.hardware.instructions.ASR_I_UN
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class UnofficialMathOperationTest {
    data class RegCarryCheck(
        val aReg: UByte,
        val memory: UByte,
        val expected: UByte,
        val negativeFlag: Boolean,
        val zeroFlag: Boolean,
        val carryFlag: Boolean,
    )

    companion object {
        @JvmStatic
        fun ancCheckFlags(): Stream<RegCarryCheck> {
            return Stream.of(
                RegCarryCheck(0x00u, 0x00u, 0x00u, false, true, false),
                RegCarryCheck(0xffu, 0x00u, 0x00u, false, true, false),
                RegCarryCheck(0xffu, 0x0fu, 0x0fu, false, false, false),
                RegCarryCheck(0x0fu, 0xf0u, 0x00u, false, true, true),
                RegCarryCheck(0xffu, 0xffu, 0xffu, true, false, true),
            )
        }

        @JvmStatic
        fun asrCheckFlags(): Stream<RegCarryCheck> {
            return Stream.of(
                RegCarryCheck(0x00u, 0x00u, 0x00u, false, true, false),
                RegCarryCheck(0xffu, 0x00u, 0x00u, false, true, false),
                RegCarryCheck(0xffu, 0x0fu, 0x07u, false, false, true),
                RegCarryCheck(0x0fu, 0xf0u, 0x00u, false, true, false),
                RegCarryCheck(0xffu, 0xffu, 0x7fu, false, false, true),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("ancCheckFlags")
    fun `ANC Immediate (ANC_I_UN_0B)`(data: RegCarryCheck) {
        val memory = BasicMemory(setupMemory(ANC_I_UN_0B, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ANC_I_UN_0B)
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
            isCarryFlag(data.carryFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("ancCheckFlags")
    fun `ANC Immediate (ANC_I_UN_2B)`(data: RegCarryCheck) {
        val memory = BasicMemory(setupMemory(ANC_I_UN_2B, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ANC_I_UN_2B)
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
            isCarryFlag(data.carryFlag)
        }
    }

    @ParameterizedTest
    @MethodSource("asrCheckFlags")
    fun `ASR Immediate (ASR_I_UN)`(data: RegCarryCheck) {
        val memory = BasicMemory(setupMemory(ASR_I_UN, data.memory))

        val interrogator = HardwareInterrogator(randomisedCpuState(aReg = data.aReg), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, ASR_I_UN)
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
            isCarryFlag(data.carryFlag)
        }
    }
}