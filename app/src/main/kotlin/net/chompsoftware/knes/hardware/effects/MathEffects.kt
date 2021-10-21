package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState

object AddWithCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val toAdd: UByte = operationState.getMemoryRead()
        val carryAmount: UByte = if (cpuState.isCarryFlag) 1u else 0u
        val sumUInt = cpuState.aReg + toAdd + carryAmount
        val sum = sumUInt.toUByte()
        val overflow = cpuState.aReg.xor(sum).and(toAdd.xor(sum)).and(0x80u) > 0u

        cpuState.isOverflowFlag = overflow
        cpuState.isCarryFlag = sumUInt > 0xffu
        cpuState.setARegWithFlags(sum)
    }

    override fun requiresCycle() = false
}

object DecrementX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags((cpuState.xReg - 1u).toUByte())
    }
}

object DecrementY : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags((cpuState.yReg - 1u).toUByte())
    }
}

object IncrementX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags((cpuState.xReg + 1u).toUByte())
    }
}

object IncrementY : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags((cpuState.yReg + 1u).toUByte())
    }
}

object ExclusiveOr : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.aReg.xor(operationState.getMemoryRead()))
    }

    override fun requiresCycle() = false
}

object OrWithAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.aReg.or(operationState.getMemoryRead()))
    }

    override fun requiresCycle() = false
}