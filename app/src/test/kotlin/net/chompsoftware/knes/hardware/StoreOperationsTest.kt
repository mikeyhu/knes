package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.HardwareInterrogator
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class StoreOperationsTest {

    @Nested
    inner class STA {
        @Test
        fun `STA ZeroPage`() {
            val memory = BasicMemory(setupMemory(STA_Z, 0x03u, NOP, 0x00u))

            val aReg = 0x01u

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
                    memoryWrite(3, aReg.toUByte())
                }
            }

            interrogator.assertCpuState {
                programCounter(2)
            }
        }
    }
}