package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


abstract class BranchOnEvaluation : Effect() {
    abstract fun branchEvaluation(cpuState: CpuState): Boolean

    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val read = operationState.memoryRead ?: throw Error("Read not performed")
        val location: Int = if (read >= 0x80u)
            -0x100 + read.toInt()
        else read.toInt()
        if (branchEvaluation(cpuState)) {
            cpuState.programCounter += location
            operationState.cyclesRemaining += 1
        }
    }

    override fun requiresCycle() = false
}

object BranchOnNotEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isZeroFlag
}

object BranchOnEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isZeroFlag
}