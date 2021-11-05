package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.isCarry

object AddWithCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val toAdd: UByte = operationState.getMemoryValue()
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

object ArithmeticShiftLeft : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val useAccumulator = operationState.memoryValue == null
        val toShift = operationState.memoryValue ?: cpuState.aReg
        val shifted = toShift.toUInt().shl(1)
        val shiftedByte = shifted.and(0xffu).toUByte()
        if (useAccumulator) {
            cpuState.setARegWithFlags(shiftedByte)
        } else {
            operationState.memoryValue = shiftedByte
            cpuState.setNegativeZeroFlags(shiftedByte)
        }
        cpuState.isCarryFlag = shifted.isCarry()
    }
}

object BitWithAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val memoryValue = operationState.getMemoryValue()
        val andByte: UByte = cpuState.aReg and memoryValue
        cpuState.isZeroFlag = andByte == UByte.MIN_VALUE

        cpuState.isNegativeFlag = memoryValue and CpuStatusPositions.NEGATIVE_BYTE_POSITION > 0u
        cpuState.isOverflowFlag = memoryValue and CpuStatusPositions.OVERFLOW_BYTE_POSITION > 0u
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
        cpuState.setARegWithFlags(cpuState.aReg.xor(operationState.getMemoryValue()))
    }

    override fun requiresCycle() = false
}

object LogicalShiftRight : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val useAccumulator = operationState.memoryValue == null
        val valueToShift = (operationState.memoryValue ?: cpuState.aReg).toUInt()
        val shifted = valueToShift.shr(1)
        val shiftedByte = shifted.and(0xffu).toUByte()
        if (useAccumulator) {
            cpuState.setARegWithFlags(shiftedByte)
        } else {
            operationState.memoryValue = shiftedByte
            cpuState.setNegativeZeroFlags(shiftedByte)
        }
        cpuState.isCarryFlag = (valueToShift and 0x1u) > 0u
    }
}


object OrWithAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.aReg.or(operationState.getMemoryValue()))
    }

    override fun requiresCycle() = false
}

object RotateLeft : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val useAccumulator = operationState.memoryValue == null
        val valueToShift = (operationState.memoryValue ?: cpuState.aReg).toUInt()
        val shifted = valueToShift.shl(1) + if (cpuState.isCarryFlag) 1u else 0u
        val shiftedByte = shifted.and(0xffu).toUByte()
        if (useAccumulator) {
            cpuState.setARegWithFlags(shiftedByte)
        } else {
            operationState.memoryValue = shiftedByte
            cpuState.setNegativeZeroFlags(shiftedByte)
        }
        cpuState.isCarryFlag = shifted.isCarry()
    }
}

object RotateRight : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val useAccumulator = operationState.memoryValue == null
        val valueToShift = (operationState.memoryValue ?: cpuState.aReg).toUInt()
        val shifted = valueToShift.shr(1) + if (cpuState.isCarryFlag) 0x80u else 0u
        val shiftedByte = shifted.and(0xffu).toUByte()
        if (useAccumulator) {
            cpuState.setARegWithFlags(shiftedByte)
        } else {
            operationState.memoryValue = shiftedByte
            cpuState.setNegativeZeroFlags(shiftedByte)
        }
        cpuState.isCarryFlag = (valueToShift and 0x1u) > 0u
    }
}