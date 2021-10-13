package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

class OperationState(
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
object Operation : EffectPipeline() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline {
        val instruction = memory[cpuState.programCounterWithIncrement()]

        return instructionMap.getOrElse(instruction) {
            throw NotImplementedError("Instruction ${instruction.toHex()} not found.")
        }
    }
}

@ExperimentalUnsignedTypes
abstract class EffectPipeline(vararg var effects: Effect) {
    open fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        if (effects.size < operationState.pipelinePosition)
            throw Error("Pipeline past end of effects")
        if (operationState.cyclesRemaining > 0) {
            operationState.cyclesRemaining -= 1
            return this
        }
        effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
        operationState.pipelinePosition++
        while (effects.size > operationState.pipelinePosition && !effects[operationState.pipelinePosition].requiresCycle()) {
            effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
            operationState.pipelinePosition++
        }
        if (effects.size > operationState.pipelinePosition)
            return this
        operationState.reset()
        return null
    }
}

@ExperimentalUnsignedTypes
class ImmediateMemoryOperation(vararg postEffects: Effect) : EffectPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryOperation(vararg postEffects: Effect) : EffectPipeline(
    ReadArgument1,
    ReadArgument2,
    AbsoluteRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageOperation(vararg postEffects: Effect) : EffectPipeline(
    ReadArgument1,
    ZeroPageRead,
    *postEffects
)

@ExperimentalUnsignedTypes
val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //Load Accumulator
    LDA_I to ImmediateMemoryOperation(ReadIntoAccumulator),
    LDA_AB to AbsoluteMemoryOperation(ReadIntoAccumulator),
    LDA_Z to ZeroPageOperation(ReadIntoAccumulator),
    LDX_I to ImmediateMemoryOperation(ReadIntoX),

    //Load X
    LDX_AB to AbsoluteMemoryOperation(ReadIntoX),
)

@ExperimentalUnsignedTypes
val instructionMap: Map<UByte, EffectPipeline> = mapOf(*instructionList).also {
    if (it.size != instructionList.size) {
        throw Error("instructionMap size incorrect")
    }
}


