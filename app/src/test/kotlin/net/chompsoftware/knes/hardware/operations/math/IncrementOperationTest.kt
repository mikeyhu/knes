package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.INX
import net.chompsoftware.knes.hardware.instructions.INY
import net.chompsoftware.knes.hardware.instructions.NOP
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExperimentalUnsignedTypes
class IncrementOperationTest {

    @ParameterizedTest()
    @CsvSource(
        "0x00u, 0x01u, false, false",
        "0xffu, 0x00u, false, true",
        "0x7fu, 0x80u, true,  false",
    )
    fun `INX - Increment X`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(INX, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(expected.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }

    @ParameterizedTest()
    @CsvSource(
        "0x00u, 0x01u, false, false",
        "0xffu, 0x00u, false, true",
        "0x7fu, 0x80u, true,  false",
    )
    fun `INY - Increment Y`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(INY, NOP))

        val interrogator = HardwareInterrogator(CpuState(yReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, INY)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            yReg(expected.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }
}