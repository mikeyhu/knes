package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExperimentalUnsignedTypes
class FlagOperationsTest {

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `CLD - clear Decimal`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(CLD, NOP))

        val interrogator = HardwareInterrogator(CpuState(isDecimalFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, CLD)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isDecimalFlag(false)
        }
    }
}