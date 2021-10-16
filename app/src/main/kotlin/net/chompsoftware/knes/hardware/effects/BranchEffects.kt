package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState


abstract class BranchOnEvaluation : Effect() {
    abstract fun branchEvaluation(cpuState: CpuState): Boolean

    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        if (branchEvaluation(cpuState)) {
            val read = operationState.getMemoryRead()
            val location: Int = if (read >= 0x80u) -0x100 + read.toInt() else read.toInt()
            val nextLocation = cpuState.programCounter + location
            val extraCycles = if (boundaryCrossed(cpuState.programCounter, nextLocation)) 2 else 1
            cpuState.programCounter = nextLocation
            operationState.cyclesRemaining += extraCycles
        }
    }

    override fun requiresCycle() = false

    private fun boundaryCrossed(previous: Int, next: Int) = previous.shr(8) != next.shr(8)
}

object BranchOnNotEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isZeroFlag
}

object BranchOnEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isZeroFlag
}

object BranchOnPLus : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isNegativeFlag
}