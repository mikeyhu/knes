package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.pageBoundaryCrossed


object ReadAtProgramCounter : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.setNextArgument(memory[cpuState.programCounterWithIncrement()])
    }
}

object ZeroPageRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.getZeroPagePosition()]
    }
}

object ZeroPageYRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.getZeroPagePosition(cpuState.yReg)]
        operationState.cyclesRemaining += 1
    }
}

object ZeroPageXRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.getZeroPagePosition(cpuState.xReg)]
        operationState.cyclesRemaining += 1
    }
}

object ZeroPageWrite : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = operationState.getMemoryRead().toInt()
    }

    override fun requiresCycle() = false
}

object ZeroPageXWrite : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = ((operationState.getMemoryRead() + cpuState.xReg) % 0x100u).toInt()
    }
}

object ZeroPageYWrite : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = ((operationState.getMemoryRead() + cpuState.yReg) % 0x100u).toInt()
    }
}

object AbsoluteRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.argumentsPosition()]
    }
}

object AbsoluteReadWithXOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val initialLocation = operationState.argumentsPosition()
        val finalLocation = initialLocation + cpuState.xReg.toInt()
        operationState.memoryRead = memory[finalLocation]
        if (pageBoundaryCrossed(initialLocation, finalLocation)) {
            operationState.cyclesRemaining += 1
        }
    }
}

object AbsoluteReadWithYOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val initialLocation = operationState.argumentsPosition()
        val finalLocation = initialLocation + cpuState.yReg.toInt()
        operationState.memoryRead = memory[finalLocation]
        if (pageBoundaryCrossed(initialLocation, finalLocation)) {
            operationState.cyclesRemaining += 1
        }
    }
}

object ArgumentsToLocation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentsToLocation()
    }

    override fun requiresCycle() = false
}

object ZeroPageToLocation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.zeroPageToLocation()
    }

    override fun requiresCycle() = false
}

object ArgumentsToLocationWithXOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentsToLocation(cpuState.xReg)
    }
}

object ArgumentsToLocationWithYOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentsToLocation(cpuState.yReg)
    }
}

object ImmediateRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[cpuState.programCounterWithIncrement()]
    }
}

object ReadLocationLow : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentLow = memory[operationState.getLocation()]
    }
}

object ReadLocationHigh : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argumentHigh = memory[operationState.getLocation() + 1]
    }
}