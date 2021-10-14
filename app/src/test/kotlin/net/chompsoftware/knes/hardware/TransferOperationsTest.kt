package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import net.chompsoftware.knes.toHexUByte
import net.chompsoftware.knes.toHexUInt

import org.junit.jupiter.params.ParameterizedTest

import org.junit.jupiter.params.provider.CsvSource


const val WITH_REGISTER_CHECK = "{0}: negativeFlag=={1} zeroFlag=={2}"
const val WITH_REGISTER_CHECK_ALL_FALSE = "0x10u,false,false"
const val WITH_REGISTER_CHECK_NEGATIVE_TRUE = "0x80u,true,false"
const val WITH_REGISTER_CHECK_ZERO_TRUE = "0x0u,false,true"

class TransferOperationsTest {

    @ParameterizedTest(name = WITH_REGISTER_CHECK)
    @CsvSource(
        WITH_REGISTER_CHECK_ALL_FALSE,
        WITH_REGISTER_CHECK_NEGATIVE_TRUE,
        WITH_REGISTER_CHECK_ZERO_TRUE
    )
    fun `TAX - Transfer Accumulator to X`(value: String, negativeFlag: Boolean, zeroFlag: Boolean) {
        val memory = BasicMemory(setupMemory(TAX, NOP))

        val interrogator = HardwareInterrogator(CpuState(aReg = value.toHexUInt()), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, TAX)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            xReg(value.toHexUByte())
            isNegativeFlag(negativeFlag)
            isZeroFlag(zeroFlag)
        }
    }
}



