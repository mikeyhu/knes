package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class BranchOperationsTest {
    @Nested
    inner class BNE {
        @Test
        fun `BNE Branch on not equal should not branch is zeroFlag is true`() {
            val memory = BasicMemory(setupMemory(BNE, 0x10u, NOP))

            val interrogator = HardwareInterrogator(CpuState(isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BNE)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `BNE Branch on not equal should branch is zeroFlag is false`() {
            val memory = BasicMemory(setupMemory(BNE, 0x10u, NOP))

            val interrogator = HardwareInterrogator(CpuState(isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BNE)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                // TODO : timings are incorrect in this state. Branches should be either 3 or 4 cycles depending on the page
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BNE Branch on not equal should branch backwards if zeroFlag is false and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BNE, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(CpuState(programCounter = 1, isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BNE)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                // TODO : timings are incorrect in this state. Branches should be either 3 or 4 cycles depending on the page
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }
    }

    @Nested
    inner class BEQ {
        @Test
        fun `BEQ Branch on equal should not branch is zeroFlag is false`() {
            val memory = BasicMemory(setupMemory(BEQ, 0x10u, NOP))

            val interrogator = HardwareInterrogator(CpuState(isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BEQ)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }

        @Test
        fun `BEQ Branch on equal should branch is zeroFlag is true`() {
            val memory = BasicMemory(setupMemory(BEQ, 0x10u, NOP))

            val interrogator = HardwareInterrogator(CpuState(isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BEQ)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                // TODO : timings are incorrect in this state. Branches should be either 3 or 4 cycles depending on the page
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BEQ Branch on equal should branch backwards if zeroFlag is true and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BEQ, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(CpuState(programCounter = 1, isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BEQ)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                // TODO : timings are incorrect in this state. Branches should be either 3 or 4 cycles depending on the page
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }
    }
}