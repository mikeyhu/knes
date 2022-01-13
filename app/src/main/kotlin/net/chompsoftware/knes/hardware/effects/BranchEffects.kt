package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.pageBoundaryCrossed


abstract class BranchOnEvaluation : Effect() {
    abstract fun branchEvaluation(cpuState: CpuState): Boolean

    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        if (branchEvaluation(cpuState)) {
            val read = operationState.getMemoryValue()
            val location: Int = if (read >= 0x80u) -0x100 + read.toInt() else read.toInt()
            val nextLocation = cpuState.programCounter + location
            val extraCycles = if (pageBoundaryCrossed(cpuState.programCounter, nextLocation)) 2 else 1
            cpuState.programCounter = nextLocation
            operationState.cyclesRemaining += extraCycles
        }
    }

    override fun requiresCycle() = false
}

object BranchOnCarryClear : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isCarryFlag
}

object BranchOnCarrySet : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isCarryFlag
}

object BranchOnEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isZeroFlag
}

object BranchOnMinus : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isNegativeFlag
}

object BranchOnNotEqual : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isZeroFlag
}

object BranchOnPLus : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isNegativeFlag
}

object BranchOnOverflowClear : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = !cpuState.isOverflowFlag
}

object BranchOnOverflowSet : BranchOnEvaluation() {
    override fun branchEvaluation(cpuState: CpuState) = cpuState.isOverflowFlag
}

