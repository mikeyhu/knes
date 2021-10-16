package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

class OperationState(
    var pipelinePosition: Int,
    var memoryRead: UByte?,
    var argument1: UByte?,
    var argument2: UByte?,
    var cyclesRemaining: Int = 0
) {
    fun absolutePosition() = toInt16(getArgument1(), getArgument2())

    fun getMemoryRead(): UByte {
        return memoryRead ?: throw Error("memoryRead was not set")
    }

    fun getArgument1(): UByte {
        return argument1 ?: throw Error("argument1 was not set")
    }

    fun getArgument2(): UByte {
        return argument2 ?: throw Error("argument2 was not set")
    }

    fun reset() {
        pipelinePosition = 0
        memoryRead = null
        argument1 = null
        argument2 = null
        cyclesRemaining = 0
    }
}

@ExperimentalUnsignedTypes
interface EffectPipeline {
    fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline?
}

@ExperimentalUnsignedTypes
object Operation : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline {
        val instruction = memory[cpuState.programCounterWithIncrement()]

        return instructionMap.getOrElse(instruction) {
            throw NotImplementedError("Instruction ${instruction.toHex()} not found at ${(cpuState.programCounter - 1).toHex()}")
        }
    }
}

@ExperimentalUnsignedTypes
open class VariableLengthPipeline(vararg val effects: Effect) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        if (effects.size < operationState.pipelinePosition)
            throw Error("Pipeline past end of effects")
        if (operationState.cyclesRemaining > 0) {
            operationState.cyclesRemaining -= 1
            return nextEffectPipeline(operationState)
        }
        effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
        operationState.pipelinePosition++
        while (effects.size > operationState.pipelinePosition && !effects[operationState.pipelinePosition].requiresCycle()) {
            effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
            operationState.pipelinePosition++
        }
        return nextEffectPipeline(operationState)
    }

    private fun nextEffectPipeline(operationState: OperationState): EffectPipeline? {
        if (effects.size > operationState.pipelinePosition || operationState.cyclesRemaining > 0)
            return this
        operationState.reset()
        return null
    }
}

@ExperimentalUnsignedTypes
class SingleEffectPipeline(val effect: Effect) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        effect.run(cpuState, memory, operationState)
        return null
    }
}

@ExperimentalUnsignedTypes
class ImmediateMemoryOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    AbsoluteRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ZeroPageRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //Branch
    BEQ to ImmediateMemoryOperation(BranchOnEqual),
    BNE to ImmediateMemoryOperation(BranchOnNotEqual),

    //Clear
    CLD to SingleEffectPipeline(ClearDecimal),

    //Compare
    CMP_I to ImmediateMemoryOperation(CompareToAccumulator),

    //Increment
    INX to SingleEffectPipeline(IncrementX),
    //Load Accumulator
    LDA_I to ImmediateMemoryOperation(ReadIntoAccumulator),
    LDA_AB to AbsoluteMemoryOperation(ReadIntoAccumulator),
    LDA_Z to ZeroPageReadOperation(ReadIntoAccumulator),
    LDX_I to ImmediateMemoryOperation(ReadIntoX),

    //Load X
    LDX_AB to AbsoluteMemoryOperation(ReadIntoX),

    //Store
    STA_Z to ZeroPageWriteOperation(StoreAccumulator),

    //Transfer
    TAX to SingleEffectPipeline(TransferAccumulatorToX),
    TXA to SingleEffectPipeline(TransferXToAccumulator),
    TXS to SingleEffectPipeline(TransferXToStackRegister),
)

@ExperimentalUnsignedTypes
val instructionMap: Map<UByte, EffectPipeline> = mapOf(*instructionList).also {
    if (it.size != instructionList.size) {
        throw Error("instructionMap size incorrect")
    }
}


