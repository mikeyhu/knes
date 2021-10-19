package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState

@ExperimentalUnsignedTypes
object CpuStatusPositions {
    const val CARRY_BYTE_POSITION: UByte = 0x1u
    const val ZERO_BYTE_POSITION: UByte = 0x2u
    const val INTERRUPT_BYTE_POSITION: UByte = 0x4u
    const val DECIMAL_BYTE_POSITION: UByte = 0x8u
    const val BREAK_BYTE_POSITION: UByte = 0x10u
    const val OVERFLOW_BYTE_POSITION: UByte = 0x40u
    const val NEGATIVE_BYTE_POSITION: UByte = 0x80u
}

object PushAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[((cpuState.stackReg--) + 0x100u).toInt()] = cpuState.aReg
    }
}

object PullAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(memory[((++cpuState.stackReg) + 0x100u).toInt()])
    }
}

object PullProcessorStatus : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val processorStatus = memory[((++cpuState.stackReg) + 0x100u).toInt()]
        cpuState.isCarryFlag = processorStatus.isSetFor(CpuStatusPositions.CARRY_BYTE_POSITION)
        cpuState.isZeroFlag = processorStatus.isSetFor(CpuStatusPositions.ZERO_BYTE_POSITION)
        cpuState.isInterruptDisabledFlag = processorStatus.isSetFor(CpuStatusPositions.INTERRUPT_BYTE_POSITION)
        cpuState.isDecimalFlag = processorStatus.isSetFor(CpuStatusPositions.DECIMAL_BYTE_POSITION)
        cpuState.isBreakCommandFlag = processorStatus.isSetFor(CpuStatusPositions.BREAK_BYTE_POSITION)
        cpuState.isOverflowFlag = processorStatus.isSetFor(CpuStatusPositions.OVERFLOW_BYTE_POSITION)
        cpuState.isNegativeFlag = processorStatus.isSetFor(CpuStatusPositions.NEGATIVE_BYTE_POSITION)
    }

    private fun UByte.isSetFor(mask: UByte) = this.and(mask) == mask
}

object PushProcessorStatus : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {

        val processorStatus = 0x20u + //bit 5 always set
                cpuState.isCarryFlag.isTrue(CpuStatusPositions.CARRY_BYTE_POSITION) +
                cpuState.isZeroFlag.isTrue(CpuStatusPositions.ZERO_BYTE_POSITION) +
                cpuState.isInterruptDisabledFlag.isTrue(CpuStatusPositions.INTERRUPT_BYTE_POSITION) +
                cpuState.isDecimalFlag.isTrue(CpuStatusPositions.DECIMAL_BYTE_POSITION) +
                CpuStatusPositions.BREAK_BYTE_POSITION + //always set
                cpuState.isOverflowFlag.isTrue(CpuStatusPositions.OVERFLOW_BYTE_POSITION) +
                cpuState.isNegativeFlag.isTrue(CpuStatusPositions.NEGATIVE_BYTE_POSITION)
        memory[((cpuState.stackReg--) + 0x100u).toInt()] = processorStatus.toUByte()
    }

    private fun Boolean.isTrue(check: UByte): UByte = if (this) check else 0u
}