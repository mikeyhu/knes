package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.isCarry

object AddWithCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val toAdd: UByte = operationState.getMemoryValue()
        addWithCarry(toAdd, cpuState)
    }

    override fun requiresCycle() = false
}

object AndWithAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.aReg.and(operationState.getMemoryValue()))
    }

    override fun requiresCycle() = false
}

object AndWithCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.aReg.and(operationState.getMemoryValue()))
        val toShift = operationState.getMemoryValue()
        val shifted = toShift.toUInt().shl(1)
        cpuState.isCarryFlag = shifted.isCarry()
    }

    override fun requiresCycle() = false
}

object AndShiftRight : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val anded = cpuState.aReg.and(operationState.getMemoryValue()).toUInt()
        val shifted = anded.shr(1)
        val shiftedByte = shifted.and(0xffu).toUByte()

        cpuState.setARegWithFlags(shiftedByte)
        cpuState.isCarryFlag = (anded and 0x1u) > 0u
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

object Decrement : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val value = (operationState.getMemoryValue() - 1u).toUByte()
        cpuState.setNegativeZeroFlags(value)
        operationState.memoryValue = value
    }
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

object Increment : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val value = (operationState.getMemoryValue() + 1u).toUByte()
        cpuState.setNegativeZeroFlags(value)
        operationState.memoryValue = value
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

object SubtractWithCarry : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val toSubtract: UByte = operationState.getMemoryValue().xor(0xffu)
        addWithCarry(toSubtract, cpuState)
    }

    override fun requiresCycle() = false
}

private fun addWithCarry(toAdd: UByte, cpuState: CpuState) {
    val carryAmount: UByte = if (cpuState.isCarryFlag) 1u else 0u
    val sumUInt = cpuState.aReg + toAdd + carryAmount
    val sum = sumUInt.toUByte()
    val overflow = cpuState.aReg.xor(sum).and(toAdd.xor(sum)).and(0x80u) > 0u

    cpuState.isOverflowFlag = overflow
    cpuState.isCarryFlag = sumUInt > 0xffu
    cpuState.setARegWithFlags(sum)
}