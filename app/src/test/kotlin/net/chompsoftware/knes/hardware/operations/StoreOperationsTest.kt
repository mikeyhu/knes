package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StoreOperationsTest {

    @Nested
    inner class STA {
        @Test
        fun `STA ZeroPage`() {
            val memory = BasicMemory(setupMemory(STA_Z, 0x03u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg), memory)

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
        fun `STA ZeroPage X Offset`() {
            val memory = BasicMemory(setupMemory(STA_ZX, 0x02u, NOP, 0x00u))

            val aReg: UByte = 0x05u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, xReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_ZX)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {}
                cycle {
                    memoryWrite(3, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STA Absolute with X offset`() {
            val memory = BasicMemory(setupMemory(STA_ABX, 0x01u, 0x04u, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, xReg = 0x02u), memory)

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
        fun `STA Absolute with X offset (wrapping)`() {
            val memory = BasicMemory(setupMemory(STA_ABX, 0xffu, 0xffu, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, xReg = 0x05u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_ABX)
                }
                cycle {
                    memoryRead(1, 0xffu)
                }
                cycle {
                    memoryRead(2, 0xffu)
                }
                cycle {}
                cycle {
                    memoryWrite(0x04, aReg)
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

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, yReg = 0x02u), memory)

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

        @Test
        fun `STA Absolute with Y offset (wrapping)`() {
            val memory = BasicMemory(setupMemory(STA_ABY, 0xffu, 0xffu, NOP, 0x00u))

            val aReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, yReg = 0x05u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_ABY)
                }
                cycle {
                    memoryRead(1, 0xffu)
                }
                cycle {
                    memoryRead(2, 0xffu)
                }
                cycle {}
                cycle {
                    memoryWrite(0x04, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }

        @Test
        fun `STA Indirect Indexed`() {
            val memory = BasicMemory(setupMemory(STA_IIY, 0xf0u, size = 0xffff))

            memory[0xf0] = 0xf0u
            memory[0xf1] = 0xeeu
            val aReg: UByte = 0x01u
            val yReg: UByte = 0x5u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_IIY)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {
                    memoryRead(0xf0, 0xf0u)
                }
                cycle {
                    memoryRead(0xf1, 0xeeu)
                }
                cycle {}
                cycle {
                    memoryWrite(0xeef5, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STA Indirect Indexed (wrapped)`() {
            val memory = BasicMemory(setupMemory(STA_IIY, 0xf0u, size = 0xffff))

            memory[0xf0] = 0xffu
            memory[0xf1] = 0xffu
            val aReg: UByte = 0x01u
            val yReg: UByte = 0x5u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_IIY)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {
                    memoryRead(0xf0, 0xffu)
                }
                cycle {
                    memoryRead(0xf1, 0xffu)
                }
                cycle {}
                cycle {
                    memoryWrite(0x04, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STA Indexed Indirect`() {
            val memory = BasicMemory(setupMemory(STA_IIX, 0xf0u, size = 0xffff))

            memory[0xf5] = 0xf0u
            memory[0xf6] = 0xeeu
            val aReg: UByte = 0x01u
            val xReg: UByte = 0x5u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_IIX)
                }
                cycle {
                    memoryRead(1, 0xf0u)
                }
                cycle {}
                cycle {
                    memoryRead(0xf5, 0xf0u)
                }
                cycle {
                    memoryRead(0xf6, 0xeeu)
                }
                cycle {
                    memoryWrite(0xeef0, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STA Indexed Indirect (wrapped)`() {
            val memory = BasicMemory(setupMemory(STA_IIX, 0xffu, NOP, NOP, 0xf0u, 0xeeu, size = 0xffff))

            memory[0xf5] = 0xf0u
            memory[0xf6] = 0xeeu
            val aReg: UByte = 0x01u
            val xReg: UByte = 0x5u

            val interrogator = HardwareInterrogator(randomisedCpuState(aReg = aReg, xReg = xReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STA_IIX)
                }
                cycle {
                    memoryRead(1, 0xffu)
                }
                cycle {}
                cycle {
                    memoryRead(0x4, 0xf0u)
                }
                cycle {
                    memoryRead(0x5, 0xeeu)
                }
                cycle {
                    memoryWrite(0xeef0, aReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }
    }

    @Nested
    inner class STX {
        @Test
        fun `STX ZeroPage`() {
            val memory = BasicMemory(setupMemory(STX_Z, 0x03u, NOP, 0x00u))

            val xReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = xReg), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = xReg, yReg = 0x01u), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = xReg, yReg = 0xffu), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(xReg = xReg), memory)

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

    @Nested
    inner class STY {
        @Test
        fun `STY ZeroPage`() {
            val memory = BasicMemory(setupMemory(STY_Z, 0x03u, NOP, 0x00u))

            val yReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STY_Z)
                }
                cycle {
                    memoryRead(1, 0x03u)
                }
                cycle {
                    memoryWrite(3, yReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STY ZeroPage X Offset`() {
            val memory = BasicMemory(setupMemory(STY_ZX, 0x02u, NOP, 0x00u))

            val yReg: UByte = 0x05u

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg, xReg = 0x01u), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STY_ZX)
                }
                cycle {
                    memoryRead(1, 0x02u)
                }
                cycle {}
                cycle {
                    memoryWrite(3, yReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STY ZeroPage X Offset should wrap`() {
            val memory = BasicMemory(setupMemory(STY_ZX, 0x04u, NOP, 0x00u))

            val yReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg, xReg = 0xffu), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STY_ZX)
                }
                cycle {
                    memoryRead(1, 0x04u)
                }
                cycle {}
                cycle {
                    memoryWrite(3, yReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `STY Absolute`() {
            val memory = BasicMemory(setupMemory(STY_AB, 0x01u, 0x04u, NOP, 0x00u))

            val yReg: UByte = 0x01u

            val interrogator = HardwareInterrogator(randomisedCpuState(yReg = yReg), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, STY_AB)
                }
                cycle {
                    memoryRead(1, 0x01u)
                }
                cycle {
                    memoryRead(2, 0x04u)
                }
                cycle {
                    memoryWrite(0x401, yReg)
                }
            }

            interrogator.assertCpuState {
                programCounter(3)
            }
        }
    }
}