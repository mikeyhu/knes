package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

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

        return instructionMap.getOrElse(instruction) {
            throw NotImplementedError("Instruction ${instruction.toHex()} not found.")
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
        while (effects.size > effectState.pipelinePosition && !effects[effectState.pipelinePosition].requiresCycle()) {
            effects[effectState.pipelinePosition].run(cpuState, memory, effectState)
            effectState.pipelinePosition++
        }
        if (effects.size > effectState.pipelinePosition)
            return this
        effectState.reset()
        return null
    }
}

@ExperimentalUnsignedTypes
class ImmediateMemoryEffectPipeline(vararg postEffects: Effect) : EffectPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryEffectPipeline(vararg postEffects: Effect) : EffectPipeline(
    AbsoluteReadArgument1,
    AbsoluteReadArgument2,
    AbsoluteRead,
    *postEffects
)

@ExperimentalUnsignedTypes
val instructionMap: Map<UByte, EffectPipeline> = mapOf(
    LDA_I to ImmediateMemoryEffectPipeline(ReadIntoAccumulator),
    LDA_AB to AbsoluteMemoryEffectPipeline(ReadIntoAccumulator),
    LDX_I to ImmediateMemoryEffectPipeline(ReadIntoX),
    LDX_AB to AbsoluteMemoryEffectPipeline(ReadIntoX),
)
