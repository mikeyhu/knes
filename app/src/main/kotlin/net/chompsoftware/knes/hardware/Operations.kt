package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

class OperationState(
    var pipelinePosition: Int,
    var memoryRead: UByte? = null,
    var location: Int? = null,
    var argument1: UByte? = null,
    var argument2: UByte? = null,
    var cyclesRemaining: Int = 0
) {
    fun absolutePosition() = toInt16(getArgument1(), getArgument2())

    fun getMemoryRead(): UByte {
        return memoryRead ?: throw Error("memoryRead was not set")
    }

    fun getLocation(): Int {
        return location ?: throw Error("location was not set")
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
        location = null
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
class DelayedSingleEffectPipeline(val effect: Effect, val delay: Int) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        if (operationState.pipelinePosition < delay) {
            operationState.pipelinePosition++
            return this
        }
        effect.run(cpuState, memory, operationState)
        operationState.reset()
        return null
    }
}

@ExperimentalUnsignedTypes
class ImmediateMemoryOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    AbsoluteRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteXMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    AbsoluteReadWithXOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteYMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    AbsoluteReadWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    ArgumentsToLocation,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteXMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    ArgumentsToLocationWithXOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteYMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    ArgumentsToLocationWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ZeroPageRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageXReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ZeroPageXRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageYReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ZeroPageYRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    ZeroPageWrite,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageYWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    ZeroPageYWrite,
    *postEffects
)

@ExperimentalUnsignedTypes
class IndirectOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadArgument1,
    ReadArgument2,
    ArgumentsToLocation,
    ReadIndirect1,
    ReadIndirect2,
    ArgumentsToLocation,
    *postEffects
)

@ExperimentalUnsignedTypes
val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //AddWithCarry
    ADC_I to ImmediateMemoryOperation(AddWithCarry),

    //Branch
    BCC to ImmediateMemoryOperation(BranchOnCarryClear),
    BCS to ImmediateMemoryOperation(BranchOnCarrySet),
    BEQ to ImmediateMemoryOperation(BranchOnEqual),
    BMI to ImmediateMemoryOperation(BranchOnMinus),
    BNE to ImmediateMemoryOperation(BranchOnNotEqual),
    BPL to ImmediateMemoryOperation(BranchOnPLus),
    BVC to ImmediateMemoryOperation(BranchOnOverflowClear),
    BVS to ImmediateMemoryOperation(BranchOnOverflowSet),

    //Break
    BRK to VariableLengthPipeline(
        PushProgramCounterHigh(1),
        PushProgramCounterLow(1),
        PushProcessorStatus(interruptOverride = false),
        LocationFromBreak,
        ReadIndirect1,
        ReadIndirect2,
        ArgumentsToLocation,
        JumpWithBreak
    ),

    //Clear
    CLC to SingleEffectPipeline(ClearCarry),
    CLD to SingleEffectPipeline(ClearDecimal),
    CLV to SingleEffectPipeline(ClearOverflow),
    CLI to SingleEffectPipeline(ClearInterrupt),

    //Compare
    CMP_I to ImmediateMemoryOperation(CompareToAccumulator),
    CMP_AB to AbsoluteMemoryReadOperation(CompareToAccumulator),
    CMP_ABX to AbsoluteXMemoryReadOperation(CompareToAccumulator),
    CMP_ABY to AbsoluteYMemoryReadOperation(CompareToAccumulator),

    CPX_I to ImmediateMemoryOperation(CompareToX),

    CPY_I to ImmediateMemoryOperation(CompareToY),

    //Decrement
    DEX to SingleEffectPipeline(DecrementX),
    DEY to SingleEffectPipeline(DecrementY),

    //Exclusive Or
    EOR_I to ImmediateMemoryOperation(ExclusiveOr),

    //Increment
    INX to SingleEffectPipeline(IncrementX),
    INY to SingleEffectPipeline(IncrementY),

    //Jump
    JMP_AB to AbsoluteMemoryLocationOperation(Jump),
    JMP_IN to IndirectOperation(Jump),
    JSR_AB to AbsoluteMemoryLocationOperation(
        NoOperation,
        PushProgramCounterHigh(-1),
        PushProgramCounterLow(-1),
        Jump
    ),

    //No Operation
    NOP to SingleEffectPipeline(NoOperation),

    //Load Accumulator
    LDA_I to ImmediateMemoryOperation(ReadIntoAccumulator),
    LDA_Z to ZeroPageReadOperation(ReadIntoAccumulator),
    LDA_AB to AbsoluteMemoryReadOperation(ReadIntoAccumulator),
    LDA_ABX to AbsoluteXMemoryReadOperation(ReadIntoAccumulator),
    LDA_ABY to AbsoluteYMemoryReadOperation(ReadIntoAccumulator),

    //Load X
    LDX_I to ImmediateMemoryOperation(ReadIntoX),
    LDX_AB to AbsoluteMemoryReadOperation(ReadIntoX),
    LDX_ABY to AbsoluteYMemoryReadOperation(ReadIntoX),
    LDX_Z to ZeroPageReadOperation(ReadIntoX),
    LDX_ZY to ZeroPageYReadOperation(ReadIntoX),

    //load Y
    LDY_I to ImmediateMemoryOperation(ReadIntoY),
    LDY_AB to AbsoluteMemoryReadOperation(ReadIntoY),
    LDY_ABX to AbsoluteXMemoryReadOperation(ReadIntoAccumulator),
    LDY_ZX to ZeroPageXReadOperation(ReadIntoY),

    //Or
    ORA_I to ImmediateMemoryOperation(OrWithAccumulator),

    //Push and Pull Stack Operations
    PHA to DelayedSingleEffectPipeline(PushAccumulator, delay = 1),
    PHP to DelayedSingleEffectPipeline(PushProcessorStatus(false), delay = 1),
    PLA to DelayedSingleEffectPipeline(PullAccumulator, delay = 2),
    PLP to DelayedSingleEffectPipeline(PullProcessorStatus, delay = 2),

    //Return
    RTI to VariableLengthPipeline(
        PullProcessorStatus,
        PullProgramCounterLow,
        PullProgramCounterHigh,
        ArgumentsToLocation,
        NoOperation,
        ClearBreak,
        Jump
    ),
    RTS to VariableLengthPipeline(
        NoOperation,
        NoOperation,
        PullProgramCounterLow,
        PullProgramCounterHigh,
        ArgumentsToLocation,
        Jump,
        IncrementProgramCounter
    ),

    //Set
    SEC to SingleEffectPipeline(SetCarry),
    SEI to SingleEffectPipeline(SetInterrupt),
    SED to SingleEffectPipeline(SetDecimal),

    //Store
    STA_Z to ZeroPageWriteOperation(StoreAccumulator),
    STA_AB to AbsoluteMemoryLocationOperation(StoreAccumulator),
    STA_ABX to AbsoluteXMemoryLocationOperation(StoreAccumulator),
    STA_ABY to AbsoluteYMemoryLocationOperation(StoreAccumulator),
    STX_Z to ZeroPageWriteOperation(StoreX),
    STX_ZY to ZeroPageYWriteOperation(StoreX),
    STX_AB to AbsoluteMemoryLocationOperation(StoreX),

    //Transfer
    TAX to SingleEffectPipeline(TransferAccumulatorToX),
    TAY to SingleEffectPipeline(TransferAccumulatorToY),
    TSX to SingleEffectPipeline(TransferStackRegisterToX),
    TXA to SingleEffectPipeline(TransferXToAccumulator),
    TXS to SingleEffectPipeline(TransferXToStackRegister),
    TYA to SingleEffectPipeline(TransferYToAccumulator),
)

@ExperimentalUnsignedTypes
val instructionMap: Map<UByte, EffectPipeline> = mapOf(*instructionList).also {
    if (it.size != instructionList.size) {
        throw Error("instructionMap size incorrect")
    }
}


