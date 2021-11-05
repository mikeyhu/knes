package net.chompsoftware.knes.hardware.operations.math

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.DEX
import net.chompsoftware.knes.hardware.instructions.DEY
import net.chompsoftware.knes.hardware.instructions.NOP
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExperimentalUnsignedTypes
class DecrementOperationTest {

    @ParameterizedTest()
    @CsvSource(
        "0x10u, 0x0fu, false, false",
        "0x01u, 0x00u, false, true",
        "0x81u, 0x80u, true, false",
        "0x0u, 0xffu, true, false",
    )
    fun `DEX - Decrement X`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(DEX, NOP))

        val interrogator = HardwareInterrogator(CpuState(xReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, DEX)
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
        "0x10u, 0x0fu, false, false",
        "0x01u, 0x00u, false, true",
        "0x81u, 0x80u, true, false",
        "0x0u, 0xffu, true, false",
    )
    fun `DEY - Decrement Y`(initial: String, expected: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(DEY, NOP))

        val interrogator = HardwareInterrogator(CpuState(yReg = initial.toHexUByte()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, DEY)
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