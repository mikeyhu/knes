package net.chompsoftware.knes.hardware.operations

import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.instructions.NOP
import net.chompsoftware.knes.hardware.instructions.RTI
import net.chompsoftware.knes.hardware.utilities.HardwareInterrogator
import net.chompsoftware.knes.hardware.utilities.randomisedCpuState
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Test


class InterruptsTest {

    /*
    From: https://www.nesdev.org/wiki/CPU_interrupts#IRQ_and_NMI_tick-by-tick_execution
        #  address R/W description
        --- ------- --- -----------------------------------------------
         1    PC     R  fetch opcode (and discard it - $00 (BRK) is forced into the opcode register instead)
         2    PC     R  read next instruction byte (actually the same as above, since PC increment is suppressed. Also discarded.)
         3  $0100,S  W  push PCH on stack, decrement S
         4  $0100,S  W  push PCL on stack, decrement S
        *** At this point, the signal status determines which interrupt vector is used ***
         5  $0100,S  W  push P on stack (with B flag *clear*), decrement S
         6   A       R  fetch PCL (A = FFFE for IRQ, A = FFFA for NMI), set I flag
         7   A       R  fetch PCH (A = FFFF for IRQ, A = FFFB for NMI)
     */

    @Test
    fun `NMI Interrupt`() {
        val memory = BasicMemory(setupMemory(NOP, size = 0x10000))

        memory[0xfffa] = 0x34u
        memory[0xfffb] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xffu,
                isNMIInterrupt = true,
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, NOP)
            }
            cycle { }
            cycle {
                memoryWrite(0x1ff, 0x00u)
            }
            cycle {
                memoryWrite(0x1fe, 0x00u)
            }
            cycle {
                memoryWrite(0x1fd, 0x30u)
            }
            cycle {
                memoryRead(0xfffa, 0x34u)
            }
            cycle {
                memoryRead(0xfffb, 0x12u)
            }

        }

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xfcu)
            isInterruptDisabledFlag(true)
            isNMIInterrupt(false)
        }
    }

    @Test
    fun `NMI Interrupt fires even when interrupt is disabled`() {
        val memory = BasicMemory(setupMemory(NOP, size = 0x10000))

        memory[0xfffa] = 0x34u
        memory[0xfffb] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xffu,
                isNMIInterrupt = true,
                isInterruptDisabledFlag = true
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xfcu)
            isInterruptDisabledFlag(true)
            isNMIInterrupt(false)
        }
    }

    @Test
    fun `NMI Interrupt - Return From Interrupt goes back to original location`() {
        val memory = BasicMemory(setupMemory(NOP, size = 0x10000))

        memory[0xfffa] = 0x34u
        memory[0xfffb] = 0x12u
        memory[0x1234] = RTI

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xffu,
                isNMIInterrupt = true
            ), memory
        )

        interrogator.processInstruction()
        interrogator.processInstruction()

        interrogator.assertCpuState {
            programCounter(0x0000)
            stackReg(0xffu)
            isNMIInterrupt(false)
        }
    }

    @Test
    fun `IRQ Interrupt`() {
        val memory = BasicMemory(setupMemory(NOP, size = 0x10000))

        memory[0xfffe] = 0x34u
        memory[0xffff] = 0x12u

        val interrogator = HardwareInterrogator(
            CpuState(
                stackReg = 0xffu,
                isIRQInterrupt = true
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCycleLog {
            cycle {
                memoryRead(0, NOP)
            }
            cycle { }
            cycle {
                memoryWrite(0x1ff, 0x00u)
            }
            cycle {
                memoryWrite(0x1fe, 0x00u)
            }
            cycle {
                memoryWrite(0x1fd, 0x30u)
            }
            cycle {
                memoryRead(0xfffe, 0x34u)
            }
            cycle {
                memoryRead(0xffff, 0x12u)
            }

        }

        interrogator.assertCpuState {
            programCounter(0x1234)
            stackReg(0xfcu)
            isInterruptDisabledFlag(true)
            isIRQInterrupt(false)
        }
    }

    @Test
    fun `IRQ Interrupt does not fire when interrupt is disabled`() {
        val memory = BasicMemory(setupMemory(NOP, size = 0x10000))

        memory[0xfffe] = 0x34u
        memory[0xffff] = 0x12u

        val interrogator = HardwareInterrogator(
            randomisedCpuState(
                stackReg = 0xffu,
                isIRQInterrupt = true,
                isNMIInterrupt = false,
                isInterruptDisabledFlag = true
            ), memory
        )

        interrogator.processInstruction()

        interrogator.assertCpuState {
            programCounter(0x01)
        }
    }
}