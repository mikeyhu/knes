package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class OperationsTest {


    @Test
    fun `NOP - No Operation`() {
        val memory = BasicMemory(setupMemory(NOP))

        val interrogator = HardwareInterrogator(CpuState(), memory)

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
                memoryWrite(0x1fe, 0x02u)//?
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
}