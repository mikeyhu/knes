package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*

@ExperimentalUnsignedTypes
val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //AddWithCarry
    ADC_I to ImmediateMemoryReadOperation(AddWithCarry),
    ADC_AB to AbsoluteMemoryReadOperation(AddWithCarry),
    ADC_ABX to AbsoluteXMemoryReadOperation(AddWithCarry),
    ADC_ABY to AbsoluteYMemoryReadOperation(AddWithCarry),
    ADC_Z to ZeroPageReadOperation(AddWithCarry),
    ADC_ZX to ZeroPageXReadOperation(AddWithCarry),

    //Branch
    BCC to ImmediateMemoryReadOperation(BranchOnCarryClear),
    BCS to ImmediateMemoryReadOperation(BranchOnCarrySet),
    BEQ to ImmediateMemoryReadOperation(BranchOnEqual),
    BMI to ImmediateMemoryReadOperation(BranchOnMinus),
    BNE to ImmediateMemoryReadOperation(BranchOnNotEqual),
    BPL to ImmediateMemoryReadOperation(BranchOnPLus),
    BVC to ImmediateMemoryReadOperation(BranchOnOverflowClear),
    BVS to ImmediateMemoryReadOperation(BranchOnOverflowSet),

    //Break
    BRK to VariableLengthPipeline(
        PushProgramCounterHigh(1),
        PushProgramCounterLow(1),
        PushProcessorStatus(interruptOverride = false),
        LocationFromBreak,
        ReadLocationLow,
        ReadLocationHigh,
        ArgumentsToLocation,
        JumpWithBreak
    ),

    //Clear
    CLC to SingleEffectPipeline(ClearCarry),
    CLD to SingleEffectPipeline(ClearDecimal),
    CLV to SingleEffectPipeline(ClearOverflow),
    CLI to SingleEffectPipeline(ClearInterrupt),

    //Compare
    CMP_I to ImmediateMemoryReadOperation(CompareToAccumulator),
    CMP_AB to AbsoluteMemoryReadOperation(CompareToAccumulator),
    CMP_ABX to AbsoluteXMemoryReadOperation(CompareToAccumulator),
    CMP_ABY to AbsoluteYMemoryReadOperation(CompareToAccumulator),
    CMP_Z to ZeroPageReadOperation(CompareToAccumulator),
    CMP_ZX to ZeroPageXReadOperation(CompareToAccumulator),
    CMP_IIY to IndirectIndexedReadOperation(CompareToAccumulator),


    CPX_I to ImmediateMemoryReadOperation(CompareToX),
    CPX_AB to AbsoluteMemoryReadOperation(CompareToX),
    CPX_Z to ZeroPageReadOperation(CompareToX),

    CPY_I to ImmediateMemoryReadOperation(CompareToY),
    CPY_AB to AbsoluteMemoryReadOperation(CompareToY),
    CPY_Z to ZeroPageReadOperation(CompareToY),

    //Decrement
    DEX to SingleEffectPipeline(DecrementX),
    DEY to SingleEffectPipeline(DecrementY),

    //Exclusive Or
    EOR_I to ImmediateMemoryReadOperation(ExclusiveOr),
    EOR_AB to AbsoluteMemoryReadOperation(ExclusiveOr),
    EOR_ABX to AbsoluteXMemoryReadOperation(ExclusiveOr),
    EOR_ABY to AbsoluteYMemoryReadOperation(ExclusiveOr),
    EOR_Z to ZeroPageReadOperation(ExclusiveOr),
    EOR_ZX to ZeroPageXReadOperation(ExclusiveOr),

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
    LDA_I to ImmediateMemoryReadOperation(ReadIntoAccumulator),
    LDA_Z to ZeroPageReadOperation(ReadIntoAccumulator),
    LDA_ZX to ZeroPageXReadOperation(ReadIntoAccumulator),
    LDA_AB to AbsoluteMemoryReadOperation(ReadIntoAccumulator),
    LDA_ABX to AbsoluteXMemoryReadOperation(ReadIntoAccumulator),
    LDA_ABY to AbsoluteYMemoryReadOperation(ReadIntoAccumulator),
    LDA_IIY to IndirectIndexedReadOperation(ReadIntoAccumulator),

    //Load X
    LDX_I to ImmediateMemoryReadOperation(ReadIntoX),
    LDX_AB to AbsoluteMemoryReadOperation(ReadIntoX),
    LDX_ABY to AbsoluteYMemoryReadOperation(ReadIntoX),
    LDX_Z to ZeroPageReadOperation(ReadIntoX),
    LDX_ZY to ZeroPageYReadOperation(ReadIntoX),

    //load Y
    LDY_I to ImmediateMemoryReadOperation(ReadIntoY),
    LDY_AB to AbsoluteMemoryReadOperation(ReadIntoY),
    LDY_ABX to AbsoluteXMemoryReadOperation(ReadIntoY),
    LDY_Z to ZeroPageReadOperation(ReadIntoY),
    LDY_ZX to ZeroPageXReadOperation(ReadIntoY),

    //Or
    ORA_I to ImmediateMemoryReadOperation(OrWithAccumulator),
    ORA_AB to AbsoluteMemoryReadOperation(OrWithAccumulator),
    ORA_ABX to AbsoluteXMemoryReadOperation(OrWithAccumulator),
    ORA_ABY to AbsoluteYMemoryReadOperation(OrWithAccumulator),
    ORA_Z to ZeroPageReadOperation(OrWithAccumulator),
    ORA_ZX to ZeroPageXReadOperation(OrWithAccumulator),

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
    STA_ZX to ZeroPageXWriteOperation(StoreAccumulator),
    STA_AB to AbsoluteMemoryLocationOperation(StoreAccumulator),
    STA_ABX to AbsoluteXMemoryLocationOperation(StoreAccumulator),
    STA_ABY to AbsoluteYMemoryLocationOperation(StoreAccumulator),
    STA_IIY to IndirectIndexedMemoryLocationOperation(StoreAccumulator),

    STX_Z to ZeroPageWriteOperation(StoreX),
    STX_ZY to ZeroPageYWriteOperation(StoreX),
    STX_AB to AbsoluteMemoryLocationOperation(StoreX),

    STY_Z to ZeroPageWriteOperation(StoreY),
    STY_ZX to ZeroPageXWriteOperation(StoreY),
    STY_AB to AbsoluteMemoryLocationOperation(StoreY),

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


