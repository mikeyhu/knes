package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class StoreOperationsTest {

    @Nested
    inner class STA {
        @Test
        fun `STA ZeroPage`() {
            val memory = BasicMemory(setupMemory(STA_Z, 0x03u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(aReg = aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryWrite(3, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STA Absolute`() {
            val memory = BasicMemory(setupMemory(STA_AB, 0x01u, 0x04u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(aReg = aReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_AB)
                }
                cycle {
                    memoryRead(1, 0x01u)
                }
                cycle {
                    memoryRead(2, 0x04u)
                }
                cycle {
                    memoryWrite(0x401, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }

        @Test
        fun `STA Absolute with X offset`() {
            val memory = BasicMemory(setupMemory(STA_ABX, 0x01u, 0x04u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(aReg = aReg, xReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_ABX)
                }
                cycle {
                    memoryRead(1, 0x01u)
                }
                cycle {
                    memoryRead(2, 0x04u)
                }
                cycle {}
                cycle {
                    memoryWrite(0x403, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }

        @Test
        fun `STA Absolute with Y offset`() {
            val memory = BasicMemory(setupMemory(STA_ABY, 0x01u, 0x04u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(aReg = aReg, yReg = 0x02u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_ABY)
                }
                cycle {
                    memoryRead(1, 0x01u)
                }
                cycle {
                    memoryRead(2, 0x04u)
                }
                cycle {}
                cycle {
                    memoryWrite(0x403, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }
    }

    @Nested
    inner class STX {
        @Test
        fun `STX ZeroPage`() {
            val memory = BasicMemory(setupMemory(STX_Z, 0x03u, NOP, 0x00u))

            val xReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STX_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryWrite(3, xReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STX ZeroPage Y Offset`() {
            val memory = BasicMemory(setupMemory(STX_ZY, 0x02u, NOP, 0x00u))

            val xReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(xReg = xReg, yReg= 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STX_ZY)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {}
                cycle {
                    memoryWrite(3, xReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STX ZeroPage Y Offset should wrap`() {
            val memory = BasicMemory(setupMemory(STX_ZY, 0x04u, NOP, 0x00u))

            val xReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(xReg = xReg, yReg= 0xffu), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STX_ZY)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {}
                cycle {
                    memoryWrite(3, xReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STX Absolute`() {
            val memory = BasicMemory(setupMemory(STX_AB, 0x01u, 0x04u, NOP, 0x00u))

            val xReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(CpuState(xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STX_AB)
                }
                cycle {
                    memoryRead(1, 0x01u)
                }
                cycle {
                    memoryRead(2, 0x04u)
                }
                cycle {
                    memoryWrite(0x401, xReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }
    }
}