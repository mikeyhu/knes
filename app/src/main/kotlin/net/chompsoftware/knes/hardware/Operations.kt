package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.hardware.instructions.*

@ExperimentalUnsignedTypes
val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //AddWithCarry
    ADC_I to ImmediateReadPipeline(AddWithCarry),
    ADC_AB to AbsoluteReadPipeline(AddWithCarry),
    ADC_ABX to AbsoluteXReadPipeline(AddWithCarry),
    ADC_ABY to AbsoluteYReadPipeline(AddWithCarry),
    ADC_Z to ZeroPageReadPipeline(AddWithCarry),
    ADC_ZX to ZeroPageXReadPipeline(AddWithCarry),

    //ArithmeticShiftLeft
    ASL_NONE to SingleEffectPipeline(ArithmeticShiftLeft),
    ASL_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(ArithmeticShiftLeft)),
    ASL_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(ArithmeticShiftLeft)),
    ASL_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(ArithmeticShiftLeft)),
    ASL_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(ArithmeticShiftLeft)),

    //BitWithAccumulator
    BIT_AB to AbsoluteReadPipeline(BitWithAccumulator),
    BIT_Z to ZeroPageReadPipeline(BitWithAccumulator),

    //Branch
    BCC to ImmediateReadPipeline(BranchOnCarryClear),
    BCS to ImmediateReadPipeline(BranchOnCarrySet),
    BEQ to ImmediateReadPipeline(BranchOnEqual),
    BMI to ImmediateReadPipeline(BranchOnMinus),
    BNE to ImmediateReadPipeline(BranchOnNotEqual),
    BPL to ImmediateReadPipeline(BranchOnPLus),
    BVC to ImmediateReadPipeline(BranchOnOverflowClear),
    BVS to ImmediateReadPipeline(BranchOnOverflowSet),

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
    CMP_I to ImmediateReadPipeline(CompareToAccumulator),
    CMP_AB to AbsoluteReadPipeline(CompareToAccumulator),
    CMP_ABX to AbsoluteXReadPipeline(CompareToAccumulator),
    CMP_ABY to AbsoluteYReadPipeline(CompareToAccumulator),
    CMP_Z to ZeroPageReadPipeline(CompareToAccumulator),
    CMP_ZX to ZeroPageXReadPipeline(CompareToAccumulator),
    CMP_IIX to IndexedIndirectReadPipeline(CompareToAccumulator),
    CMP_IIY to IndirectIndexedReadPipeline(CompareToAccumulator),


    CPX_I to ImmediateReadPipeline(CompareToX),
    CPX_AB to AbsoluteReadPipeline(CompareToX),
    CPX_Z to ZeroPageReadPipeline(CompareToX),

    CPY_I to ImmediateReadPipeline(CompareToY),
    CPY_AB to AbsoluteReadPipeline(CompareToY),
    CPY_Z to ZeroPageReadPipeline(CompareToY),

    //Decrement
    DEX to SingleEffectPipeline(DecrementX),
    DEY to SingleEffectPipeline(DecrementY),

    //Exclusive Or
    EOR_I to ImmediateReadPipeline(ExclusiveOr),
    EOR_AB to AbsoluteReadPipeline(ExclusiveOr),
    EOR_ABX to AbsoluteXReadPipeline(ExclusiveOr),
    EOR_ABY to AbsoluteYReadPipeline(ExclusiveOr),
    EOR_Z to ZeroPageReadPipeline(ExclusiveOr),
    EOR_ZX to ZeroPageXReadPipeline(ExclusiveOr),

    //Increment
    INX to SingleEffectPipeline(IncrementX),
    INY to SingleEffectPipeline(IncrementY),

    //Jump
    JMP_AB to AbsoluteLocationPipeline(Jump),
    JMP_IN to IndirectPipeline(Jump),
    JSR_AB to AbsoluteLocationPipeline(
        NoOperation,
        PushProgramCounterHigh(-1),
        PushProgramCounterLow(-1),
        Jump
    ),

    //No Operation
    NOP to SingleEffectPipeline(NoOperation),

    //Load Accumulator
    LDA_I to ImmediateReadPipeline(ReadIntoAccumulator),
    LDA_Z to ZeroPageReadPipeline(ReadIntoAccumulator),
    LDA_ZX to ZeroPageXReadPipeline(ReadIntoAccumulator),
    LDA_AB to AbsoluteReadPipeline(ReadIntoAccumulator),
    LDA_ABX to AbsoluteXReadPipeline(ReadIntoAccumulator),
    LDA_ABY to AbsoluteYReadPipeline(ReadIntoAccumulator),
    LDA_IIY to IndirectIndexedReadPipeline(ReadIntoAccumulator),
    LDA_IIX to IndexedIndirectReadPipeline(ReadIntoAccumulator),

    //Load X
    LDX_I to ImmediateReadPipeline(ReadIntoX),
    LDX_AB to AbsoluteReadPipeline(ReadIntoX),
    LDX_ABY to AbsoluteYReadPipeline(ReadIntoX),
    LDX_Z to ZeroPageReadPipeline(ReadIntoX),
    LDX_ZY to ZeroPageYReadPipeline(ReadIntoX),

    //load Y
    LDY_I to ImmediateReadPipeline(ReadIntoY),
    LDY_AB to AbsoluteReadPipeline(ReadIntoY),
    LDY_ABX to AbsoluteXReadPipeline(ReadIntoY),
    LDY_Z to ZeroPageReadPipeline(ReadIntoY),
    LDY_ZX to ZeroPageXReadPipeline(ReadIntoY),

    LSR_NONE to SingleEffectPipeline(LogicalShiftRight),
    LSR_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(LogicalShiftRight)),
    LSR_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(LogicalShiftRight)),
    LSR_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(LogicalShiftRight)),
    LSR_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(LogicalShiftRight)),

    //Or
    ORA_I to ImmediateReadPipeline(OrWithAccumulator),
    ORA_AB to AbsoluteReadPipeline(OrWithAccumulator),
    ORA_ABX to AbsoluteXReadPipeline(OrWithAccumulator),
    ORA_ABY to AbsoluteYReadPipeline(OrWithAccumulator),
    ORA_Z to ZeroPageReadPipeline(OrWithAccumulator),
    ORA_ZX to ZeroPageXReadPipeline(OrWithAccumulator),

    //Rotate Left
    ROL_NONE to SingleEffectPipeline(RotateLeft),
    ROL_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(RotateLeft)),
    ROL_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(RotateLeft)),
    ROL_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(RotateLeft)),
    ROL_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(RotateLeft)),

    //Rotate Right
    ROR_NONE to SingleEffectPipeline(RotateRight),
    ROR_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(RotateRight)),
    ROR_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(RotateRight)),
    ROR_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(RotateRight)),
    ROR_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(RotateRight)),

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
    STA_Z to ZeroPageLocationPipeline(StoreAccumulator),
    STA_ZX to ZeroPageXLocationPipeline(StoreAccumulator),
    STA_AB to AbsoluteLocationPipeline(StoreAccumulator),
    STA_ABX to AbsoluteXLocationPipeline(StoreAccumulator),
    STA_ABY to AbsoluteYLocationPipeline(StoreAccumulator),
    STA_IIY to IndirectIndexedLocationPipeline(StoreAccumulator),
    STA_IIX to IndexedIndirectLocationPipeline(StoreAccumulator),

    STX_Z to ZeroPageLocationPipeline(StoreX),
    STX_ZY to ZeroPageYLocationPipeline(StoreX),
    STX_AB to AbsoluteLocationPipeline(StoreX),

    STY_Z to ZeroPageLocationPipeline(StoreY),
    STY_ZX to ZeroPageXLocationPipeline(StoreY),
    STY_AB to AbsoluteLocationPipeline(StoreY),

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


