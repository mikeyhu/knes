package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


object TransferAccumulatorToX : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(cpuState.aReg)
    }
}

object TransferAccumulatorToY : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags(cpuState.aReg)
    }
}

object TransferXToAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.xReg)
    }
}

object TransferYToAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.yReg)
    }
}

object TransferXToStackRegister : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.stackReg = cpuState.xReg
    }
}

object TransferStackRegisterToX : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(cpuState.stackReg)
    }
}