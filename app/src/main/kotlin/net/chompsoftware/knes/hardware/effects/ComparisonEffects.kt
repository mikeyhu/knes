package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


object CompareToAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setComparisonFlags(cpuState.aReg, operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object CompareToX : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setComparisonFlags(cpuState.xReg, operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object CompareToY : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setComparisonFlags(cpuState.yReg, operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}