package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.maskedEquals

object CpuStatusPositions {
    const val CARRY_BYTE_POSITION: UByte = 0x1u
    const val ZERO_BYTE_POSITION: UByte = 0x2u
    const val INTERRUPT_BYTE_POSITION: UByte = 0x4u
    const val DECIMAL_BYTE_POSITION: UByte = 0x8u
    const val BREAK_BYTE_POSITION: UByte = 0x10u
    const val OVERFLOW_BYTE_POSITION: UByte = 0x40u
    const val NEGATIVE_BYTE_POSITION: UByte = 0x80u
}

object CpuStatusComparisons {
    const val ZERO_FLAG: UByte = 0x0u
    const val NEGATIVE_FLAG: UByte = 0x80u
}

object PushAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[((cpuState.stackReg--) + 0x100u).toInt()] = cpuState.aReg
    }
}

object PullAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(memory[((++cpuState.stackReg) + 0x100u).toInt()])
    }
}

object PullProcessorStatus : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val processorStatus = memory[((++cpuState.stackReg) + 0x100u).toInt()]
        cpuState.isCarryFlag = processorStatus.maskedEquals(CpuStatusPositions.CARRY_BYTE_POSITION)
        cpuState.isZeroFlag = processorStatus.maskedEquals(CpuStatusPositions.ZERO_BYTE_POSITION)
        cpuState.isInterruptDisabledFlag = processorStatus.maskedEquals(CpuStatusPositions.INTERRUPT_BYTE_POSITION)
        cpuState.isDecimalFlag = processorStatus.maskedEquals(CpuStatusPositions.DECIMAL_BYTE_POSITION)
        cpuState.isBreakCommandFlag = processorStatus.maskedEquals(CpuStatusPositions.BREAK_BYTE_POSITION)
        cpuState.isOverflowFlag = processorStatus.maskedEquals(CpuStatusPositions.OVERFLOW_BYTE_POSITION)
        cpuState.isNegativeFlag = processorStatus.maskedEquals(CpuStatusPositions.NEGATIVE_BYTE_POSITION)
    }
}

class PushProcessorStatus(
    val interruptOverride: Boolean = false
) : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {

        val processorStatus = 0x20u + //bit 5 always set
                cpuState.isCarryFlag.isTrue(CpuStatusPositions.CARRY_BYTE_POSITION) +
                cpuState.isZeroFlag.isTrue(CpuStatusPositions.ZERO_BYTE_POSITION) +
                cpuState.isInterruptDisabledFlag.isTrue(CpuStatusPositions.INTERRUPT_BYTE_POSITION, interruptOverride) +
                cpuState.isDecimalFlag.isTrue(CpuStatusPositions.DECIMAL_BYTE_POSITION) +
                CpuStatusPositions.BREAK_BYTE_POSITION + //always set
                cpuState.isOverflowFlag.isTrue(CpuStatusPositions.OVERFLOW_BYTE_POSITION) +
                cpuState.isNegativeFlag.isTrue(CpuStatusPositions.NEGATIVE_BYTE_POSITION)
        memory[((cpuState.stackReg--) + 0x100u).toInt()] = processorStatus.toUByte()
    }

    private fun Boolean.isTrue(check: UByte): UByte = if (this) check else 0u
    private fun Boolean.isTrue(check: UByte, override:Boolean): UByte = if (this || override) check else 0u
}

class PushProgramCounterLow(val alterBeforeWrite:Int) : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[((cpuState.stackReg--) + 0x100u).toInt()] =
            (cpuState.programCounter + alterBeforeWrite).toUInt().and(0xffu).toUByte()
    }
}

class PushProgramCounterHigh(val alterBeforeWrite:Int) : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[((cpuState.stackReg--) + 0x100u).toInt()] =
            (cpuState.programCounter + alterBeforeWrite).toUInt().and(0xff00u).shr(8).toUByte()
    }
}

object PullProgramCounterLow : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentLow = memory[((++cpuState.stackReg) + 0x100u).toInt()]
    }
}

object PullProgramCounterHigh : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentHigh = memory[((++cpuState.stackReg) + 0x100u).toInt()]
    }
}