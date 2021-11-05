package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.instructions.*
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class BranchOperationsTest {
    @Nested
    inner class BCC {
        @Test
        fun `BCC Branch on carry clear should not branch is carryFlag is true`() {
            val memory = BasicMemory(setupMemory(BCC, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BCC)
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
        fun `BCC Branch on carry clear should branch is carryFlag is false`() {
            val memory = BasicMemory(setupMemory(BCC, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BCC)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BCC Branch on carry clear should branch backwards if carryFlag is false and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BCC, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 1, isCarryFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BCC)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }

        @Test
        fun `BCC Branch on carry clear should take an extra cycle if crossing a page boundary when branching`() {
            val memory = BasicMemory(setupMemory(BCC, 0x10u, NOP, memoryOffset = 0xf0))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 0xf0, isCarryFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0xf0, BCC)
                }
                cycle {
                    memoryRead(0xf1, 0x10u)
                }
                cycle {}
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0xf2 + 0x10)
            }
        }
    }

    @Nested
    inner class BCS {
        @Test
        fun `BCS Branch on carry set should not branch is carryFlag is false`() {
            val memory = BasicMemory(setupMemory(BCS, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BCS)
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
        fun `BCS Branch on carry set should branch is carryFlag is true`() {
            val memory = BasicMemory(setupMemory(BCS, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isCarryFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BCS)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }
    }

    @Nested
    inner class BNE {
        @Test
        fun `BNE Branch on not equal should not branch is zeroFlag is true`() {
            val memory = BasicMemory(setupMemory(BNE, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isZeroFlag = true), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BNE)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BNE Branch on not equal should branch backwards if zeroFlag is false and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BNE, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 1, isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BNE)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }

        @Test
        fun `BNE Branch on not equal should take an extra cycle if crossing a page boundary when branching`() {
            val memory = BasicMemory(setupMemory(BNE, 0x10u, NOP, memoryOffset = 0xf0))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 0xf0, isZeroFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0xf0, BNE)
                }
                cycle {
                    memoryRead(0xf1, 0x10u)
                }
                cycle {}
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0xf2 + 0x10)
            }
        }
    }

    @Nested
    inner class BMI {
        @Test
        fun `BMI Branch on minus should not branch is negativeFlag is false`() {
            val memory = BasicMemory(setupMemory(BMI, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isNegativeFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BMI)
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
        fun `BMI Branch on minus should branch is negativeFlag is true`() {
            val memory = BasicMemory(setupMemory(BMI, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isNegativeFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BMI)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }
    }

    @Nested
    inner class BEQ {
        @Test
        fun `BEQ Branch on equal should not branch if zeroFlag is false`() {
            val memory = BasicMemory(setupMemory(BEQ, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isZeroFlag = false), memory)

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

            val interrogator = HardwareInterrogator(randomisedCpuState(isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BEQ)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BEQ Branch on equal should branch backwards if zeroFlag is true and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BEQ, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 1, isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BEQ)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }

        @Test
        fun `BEQ Branch on equal should take an extra cycle if crossing a page boundary when branching`() {
            val memory = BasicMemory(setupMemory(BEQ, 0x10u, NOP, memoryOffset = 0xf0))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 0xf0, isZeroFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0xf0, BEQ)
                }
                cycle {
                    memoryRead(0xf1, 0x10u)
                }
                cycle {}
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0xf2 + 0x10)
            }
        }
    }

    @Nested
    inner class BPL {
        @Test
        fun `BPL Branch on not equal should not branch is negativeFlag is true`() {
            val memory = BasicMemory(setupMemory(BPL, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isNegativeFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BPL)
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
        fun `BPL Branch on not equal should branch is negativeFlag is false`() {
            val memory = BasicMemory(setupMemory(BPL, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isNegativeFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BPL)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }

        @Test
        fun `BPL Branch on not equal should branch backwards if negativeFlag is false and location is greater that 0x80`() {
            val memory = BasicMemory(setupMemory(NOP, BPL, 0xfdu, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 1, isNegativeFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(1, BPL)
                }
                cycle {
                    memoryRead(2, 0xfdu)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x3 - 0x3) // 3 - (0xff - 0xfd)
            }
        }

        @Test
        fun `BPL Branch on not equal should take an extra cycle if crossing a page boundary when branching`() {
            val memory = BasicMemory(setupMemory(BPL, 0x10u, NOP, memoryOffset = 0xf0))

            val interrogator = HardwareInterrogator(randomisedCpuState(programCounter = 0xf0, isNegativeFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0xf0, BPL)
                }
                cycle {
                    memoryRead(0xf1, 0x10u)
                }
                cycle {}
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0xf2 + 0x10)
            }
        }
    }

    @Nested
    inner class BVC {
        @Test
        fun `BVC Branch on overflow clear should not branch is overflowFlag is true`() {
            val memory = BasicMemory(setupMemory(BVC, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isOverflowFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BVC)
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
        fun `BVC Branch on overflow clear should branch is carryFlag is false`() {
            val memory = BasicMemory(setupMemory(BVC, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isOverflowFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BVC)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }
    }

    @Nested
    inner class BVS {
        @Test
        fun `BVS Branch on overflow set should not branch is overflowFlag is false`() {
            val memory = BasicMemory(setupMemory(BVS, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isOverflowFlag = false), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BVS)
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
        fun `BVS Branch on overflow set should branch is carryFlag is true`() {
            val memory = BasicMemory(setupMemory(BVS, 0x10u, NOP))

            val interrogator = HardwareInterrogator(randomisedCpuState(isOverflowFlag = true), memory)

            interrogator.processInstruction()

            interrogator.assertCycleLog {
                cycle {
                    memoryRead(0, BVS)
                }
                cycle {
                    memoryRead(1, 0x10u)
                }
                cycle {}
            }

            interrogator.assertCpuState {
                programCounter(0x2 + 0x10)
            }
        }
    }
}