package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.toInt16

abstract class Effect {
    @ExperimentalUnsignedTypes
    abstract fun run(cpuState: CpuState, memory: Memory, effectState: EffectState)
}

class EffectState(
    var pipelinePosition: Int,
    var memoryRead: UByte?,
    var argument1: UByte?,
    var argument2: UByte?,
    var cyclesRemaining: Int = 0
) {
    fun absolutePosition() = toInt16(
        argument1 ?: throw Error("argument1 not available"),
        argument2 ?: throw Error("argument2 not available")
    )

    fun reset() {
        pipelinePosition = 0
        memoryRead = null
        argument1 = null
        argument2 = null
        cyclesRemaining = 0
    }
}

@ExperimentalUnsignedTypes
object InstructionCheckEffectPipeline : EffectPipeline() {
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState): EffectPipeline {
        val instruction = memory[cpuState.programCounterWithIncrement()]
        return when (instruction) {
            LDA_I -> LDA_Immediate
            LDA_AB -> LDA_Absolute
            LDX_I -> LDX_Immediate
            else -> throw NotImplementedError()
        }
    }
}

@ExperimentalUnsignedTypes
abstract class EffectPipeline(vararg var effects: Effect) {
    open fun run(cpuState: CpuState, memory: Memory, effectState: EffectState): EffectPipeline? {
        if (effects.size < effectState.pipelinePosition)
            throw Error("Pipeline past end of effects")
        if (effectState.cyclesRemaining > 0) {
            effectState.cyclesRemaining -= 1
            return this
        }
        effects[effectState.pipelinePosition].run(cpuState, memory, effectState)
        effectState.pipelinePosition++
        if (effects.size > effectState.pipelinePosition)
            return this
        effectState.reset()
        return null
    }
}

@ExperimentalUnsignedTypes
object LDA_Immediate : EffectPipeline(
    Combination(ImmediateRead, ReadIntoAccumulator)
)

@ExperimentalUnsignedTypes
object LDA_Absolute : EffectPipeline(
    AbsoluteReadArgument1,
    AbsoluteReadArgument2,
    Combination(AbsoluteRead, ReadIntoAccumulator)
)

@ExperimentalUnsignedTypes
object LDX_Immediate : EffectPipeline(
    Combination(ImmediateRead, ReadIntoX)
)

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
        val read = (effectState.memoryRead ?: throw Error("Read not performed")).toUInt()
        cpuState.aReg = read
        cpuState.isNegativeFlag = tweakNegative(read)
        cpuState.isZeroFlag = tweakZero(read)
    }
}

object ReadIntoX : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        val read = (effectState.memoryRead ?: throw Error("Read not performed")).toUInt()
        cpuState.xReg = read
        cpuState.isNegativeFlag = tweakNegative(read)
        cpuState.isZeroFlag = tweakZero(read)
    }
}

class Combination(vararg val effects: Effect) : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, effectState: EffectState) {
        for (effect in effects) {
            effect.run(cpuState, memory, effectState)
        }
    }
}

private fun tweakNegative(value: UInt) = value.and(NEGATIVE_BYTE_POSITION) > 0u
private fun tweakZero(value: UInt) = value == 0u

