package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.pageBoundaryCrossed

abstract class Effect {
    @ExperimentalUnsignedTypes
    abstract fun run(cpuState: CpuState, memory: Memory, operationState: OperationState)
    open fun requiresCycle(): Boolean = true
}

object ReadArgument1 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argument1 = memory[cpuState.programCounterWithIncrement()]
    }
}

object ReadArgument2 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argument2 = memory[cpuState.programCounterWithIncrement()]
    }
}

object ZeroPageRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.getArgument1().toInt()]
    }
}

object ZeroPageYRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[((operationState.getArgument1() + cpuState.yReg) % 0x100u).toInt()]
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

object ZeroPageYWrite : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = ((operationState.getMemoryRead() + cpuState.yReg) % 0x100u).toInt()
    }
}

object AbsoluteRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[operationState.absolutePosition()]
    }
}

object AbsoluteReadWithXOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val initialLocation = operationState.absolutePosition()
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
        val initialLocation = operationState.absolutePosition()
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
        operationState.location = operationState.absolutePosition()
    }

    override fun requiresCycle() = false
}

object ArgumentsToLocationWithYOffset : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = operationState.absolutePosition() + cpuState.yReg.toInt()
    }
}

object ImmediateRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[cpuState.programCounterWithIncrement()]
    }
}

object ReadIndirect1 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argument1 = memory[operationState.getLocation()]
    }
}

object ReadIndirect2 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.argument2 = memory[operationState.getLocation() + 1]
    }
}


object ReadIntoAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(operationState.getMemoryRead())
    }

    override fun requiresCycle() = false
}

object ReadIntoX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(operationState.getMemoryRead())
    }

    override fun requiresCycle() = false
}

object ReadIntoY : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags(operationState.getMemoryRead())
    }

    override fun requiresCycle() = false
}

object StoreAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.aReg
    }
}

object StoreX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.xReg
    }
}

object NoOperation : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
    }
}

object IncrementProgramCounter : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.programCounterWithIncrement()
    }
}

object LocationFromBreak : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = cpuState.breakLocation
    }

    override fun requiresCycle() = false
}






