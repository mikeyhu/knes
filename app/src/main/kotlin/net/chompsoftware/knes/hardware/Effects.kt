package net.chompsoftware.knes.hardware

abstract class Effect {
    @ExperimentalUnsignedTypes
    abstract fun run(cpuState: CpuState, memory: Memory, effectState: EffectState)
    open fun requiresCycle(): Boolean = true
}

object AbsoluteReadArgument1 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        effectState.argument1 = memory[cpuState.programCounterWithIncrement()]
    }
}

object AbsoluteReadArgument2 : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        effectState.argument2 = memory[cpuState.programCounterWithIncrement()]
    }
}

object AbsoluteRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        if (effectState.argument1 == null) throw Error("argument1 not supplied")
        if (effectState.argument2 == null) throw Error("argument2 not supplied")
        effectState.memoryRead = memory[effectState.absolutePosition()]
    }
}

object ImmediateRead : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        effectState.memoryRead = memory[cpuState.programCounterWithIncrement()]
    }
}


object ReadIntoAccumulator : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        val read = effectState.memoryRead ?: throw Error("Read not performed")
        cpuState.setARegWithFlags(read)
    }

    override fun requiresCycle() = false
}

object ReadIntoX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        val read = effectState.memoryRead ?: throw Error("Read not performed")
        cpuState.setXRegWithFlags(read)
    }

    override fun requiresCycle() = false
}


