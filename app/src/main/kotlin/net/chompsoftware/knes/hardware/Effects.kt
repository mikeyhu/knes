package net.chompsoftware.knes.hardware

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
        if (operationState.argument1 == null) throw Error("argument1 not supplied")
        operationState.memoryRead = memory[operationState.argument1!!.toInt()]
    }
}

object AbsoluteRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        if (operationState.argument1 == null) throw Error("argument1 not supplied")
        if (operationState.argument2 == null) throw Error("argument2 not supplied")
        operationState.memoryRead = memory[operationState.absolutePosition()]
    }
}

object ImmediateRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.memoryRead = memory[cpuState.programCounterWithIncrement()]
    }
}


object ReadIntoAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val read = operationState.memoryRead ?: throw Error("Read not performed")
        cpuState.setARegWithFlags(read)
    }

    override fun requiresCycle() = false
}

object ReadIntoX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        val read = operationState.memoryRead ?: throw Error("Read not performed")
        cpuState.setXRegWithFlags(read)
    }

    override fun requiresCycle() = false
}

object TransferAccumulatorToX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(cpuState.getAReg())
    }
}

object TransferXToAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(cpuState.getXReg())
    }
}

object IncrementX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags((cpuState.getXReg() + 1u).toUByte())
    }
}


