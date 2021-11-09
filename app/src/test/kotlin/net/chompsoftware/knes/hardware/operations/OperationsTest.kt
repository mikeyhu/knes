package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class OperationsTest {

    companion object {
        @JvmStatic
        fun unofficalNops(): Stream<Int> {
            return listOf(NOP_UN_1A, NOP_UN_3A, NOP_UN_5A, NOP_UN_7A, NOP_UN_DA, NOP_UN_FA).map { it.toInt() }.stream()
        }
    }

    @Test
    fun `NOP - No Operation`() {
        val memory = BasicMemory(setupMemory(NOP))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, NOP)
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
        }
    }

    @ParameterizedTest
    @MethodSource("unofficalNops")
    fun `NOP - No Operation - unofficial`(nop: Int) {
        val memory = BasicMemory(setupMemory(nop.toUByte()))

        val interrogator = HardwareInterrogator(randomisedCpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, nop.toUByte())
            }
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(1)
        }
    }

    @Test
    fun `BRK - Break`() {
        val memory = BasicMemory(setupMemory(BRK, NOP))

        memory[0x2fe] = 0x34u
        memory[0x2ff] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                breakLocation = 0x2fe,
                stackReg = 0xffu
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, BRK)
            }
            cycle {
                memoryWrite(0x1ff, 0x00u)
            }
            cycle {
                memoryWrite(0x1fe, 0x02u)
            }
            cycle {
                memoryWrite(0x1fd, 0x30u)
            }
            cycle {
                memoryRead(0x2fe, 0x34u)
            }
            cycle {
                memoryRead(0x2ff, 0x12u)
            }

        }

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xfcu)
            isInterruptDisabledFlag(true)
        }
    }

    @Test
    fun `RTI - Return from break`() {
        val memory = BasicMemory(setupMemory(RTI, NOP))

        memory[0x1fd] = 0x30u
        memory[0x1fe] = 0x34u
        memory[0x1ff] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xfcu
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, RTI)
            }
            cycle {
                memoryRead(0x1fd, 0x30u)
            }
            cycle {
                memoryRead(0x1fe, 0x34u)
            }
            cycle {
                memoryRead(0x1ff, 0x12u)
            }
            cycle {}
            cycle {}
        }

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xffu)
            isBreakCommandFlag(false)
        }
    }
}