package net.chompsoftware.knes.hardware

abstract class Effect {
    @ExperimentalUnsignedTypes
    abstract fun run(cpuState: CpuState, memory: Memory, operationState: OperationState)
    open fun requiresCycle(): Boolean = true
}

object ReadIntoAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object ReadIntoX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object ReadIntoY : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags(operationState.getMemoryValue())
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

object StoreY : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.yReg
    }
}

object StoreMemory : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = operationState.getMemoryValue()
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






