package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExperimentalUnsignedTypes
class FlagOperationsTest {

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `CLD - clear Decimal`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(CLD, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isDecimalFlag = flagValue), memory)

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

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `CLC - clear Carry`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(CLC, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, CLC)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isCarryFlag(false)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `CLV - clear Overflow`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(CLV, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isOverflowFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, CLV)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isOverflowFlag(false)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `CLI - clear Interrupt`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(CLI, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isInterruptDisabledFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, CLI)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isInterruptDisabledFlag(false)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `SEC - set Carry`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(SEC, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SEC)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isCarryFlag(true)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `SEI - set Interrupt`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(SEI, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isInterruptDisabledFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SEI)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isInterruptDisabledFlag(true)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `SED - set decimal`(flagValue: Boolean) {
        val memory = BasicMemory(setupMemory(SED, NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(isDecimalFlag = flagValue), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, SED)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
            isDecimalFlag(true)
        }
    }
}