package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import kotlin.test.Test

@ExperimentalUnsignedTypes
class LoadOperationsTest {
    @Test
    fun `LDA Immediate`() {
        val memory = BasicMemory(setupMemory(LDA_I, 0x01u))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LDA_I)
            }
            cycle {
                memoryRead(1, 0x01u)
            }
        }

        interrogator.assertCpuState {
            assertProgramCounter(2)
            assertAReg(0x1u)
        }
    }

    @Test
    fun `LDA Immediate with Negative Flag set`() {
        val memory = BasicMemory(setupMemory(LDA_I, 0x81u))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCpuState {
            assertProgramCounter(2)
            assertAReg(0x81u)
            assertIsNegativeFlag(true)
        }

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LDA_I)
            }
            cycle {
                memoryRead(1, 0x81u)
            }
        }
    }

    @Test
    fun `LDA Absolute`() {
        val memory = BasicMemory(setupMemory(LDA_AB, 0x04u, 0x00u, NOP, 0x99u))

        val interrogator = HardwareInterrogator(CpuState(), memory)

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, LDA_AB)
            }
            cycle {
                memoryRead(1, 0x4u)
            }
            cycle {
                memoryRead(2, 0x0u)
            }
            cycle {
                memoryRead(4, 0x99u)
            }
        }

        interrogator.assertCpuState {
            assertProgramCounter(3)
            assertAReg(0x99u)
            assertIsNegativeFlag(true)
        }
    }
}