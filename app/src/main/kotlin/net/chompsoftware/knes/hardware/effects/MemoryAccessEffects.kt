package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
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
        operationState.memoryValue = memory[operationState.getZeroPagePosition()]
    }
}

object ZeroPageYRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryValue = memory[operationState.getZeroPagePosition(cpuState.yReg)]
        operationState.cyclesRemaining += 1
    }
}

object ZeroPageXRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryValue = memory[operationState.getZeroPagePosition(cpuState.xReg)]
        operationState.cyclesRemaining += 1
    }
}

object AbsoluteRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryValue = memory[operationState.argumentsPosition()]
    }
}

object AbsoluteReadWithXOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val initialLocation = operationState.argumentsPosition()
        val finalLocation = initialLocation + cpuState.xReg.toInt()
        operationState.memoryValue = memory[finalLocation]
        if (pageBoundaryCrossed(initialLocation, finalLocation)) {
            operationState.cyclesRemaining += 1
        }
    }
}

object AbsoluteReadWithYOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val initialLocation = operationState.argumentsPosition()
        val finalLocation = (initialLocation + cpuState.yReg.toInt()) % 0x10000
        operationState.memoryValue = memory[finalLocation]
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

object ZeroPageXToLocation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.zeroPageToLocation(cpuState.xReg)
    }
}

object ZeroPageYToLocation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.zeroPageToLocation(cpuState.yReg)
    }
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
        operationState.memoryValue = memory[cpuState.programCounterWithIncrement()]
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

object ReadLocationHighWithWrap : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val wrappedLocation = if (operationState.getLocation()
                .and(0xff) == 0xff
        ) operationState.getLocation() - 0xff else operationState.getLocation() + 1
        operationState.argumentHigh = memory[wrappedLocation]
    }
}

object MemoryReadFromLocation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryValue = memory[operationState.getLocation()]
    }
}