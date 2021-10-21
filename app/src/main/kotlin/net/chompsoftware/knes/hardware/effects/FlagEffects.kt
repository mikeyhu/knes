package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


object ClearCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isCarryFlag = false
    }
}

object ClearDecimal : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isDecimalFlag = false
    }
}

object ClearOverflow : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isOverflowFlag = false
    }
}

object ClearBreak : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.isBreakCommandFlag = false
    }
}