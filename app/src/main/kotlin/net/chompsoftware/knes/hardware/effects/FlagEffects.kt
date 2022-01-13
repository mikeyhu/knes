package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


object ClearCarry : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isCarryFlag = false
    }
}

object ClearDecimal : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isDecimalFlag = false
    }
}

object ClearOverflow : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isOverflowFlag = false
    }
}

object ClearBreak : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isBreakCommandFlag = false
    }
}

object ClearInterrupt : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isInterruptDisabledFlag = false
    }
}

object SetCarry : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isCarryFlag = true
    }
}

object SetDecimal : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isDecimalFlag = true
    }
}

object SetInterrupt : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isInterruptDisabledFlag = true
    }
}