package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.hardware.instructions.*

val instructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    //AddWithCarry
    ADC_I to ImmediateReadPipeline(AddWithCarry),
    ADC_AB to AbsoluteReadPipeline(AddWithCarry),
    ADC_ABX to AbsoluteXReadPipeline(AddWithCarry),
    ADC_ABY to AbsoluteYReadPipeline(AddWithCarry),
    ADC_Z to ZeroPageReadPipeline(AddWithCarry),
    ADC_ZX to ZeroPageXReadPipeline(AddWithCarry),
    ADC_IIX to IndexedIndirectReadPipeline(AddWithCarry),
    ADC_IIY to IndirectIndexedReadPipeline(AddWithCarry),


    //AndWithAccumulator
    AND_I to ImmediateReadPipeline(AndWithAccumulator),
    AND_Z to ZeroPageReadPipeline(AndWithAccumulator),
    AND_ZX to ZeroPageXReadPipeline(AndWithAccumulator),
    AND_AB to AbsoluteReadPipeline(AndWithAccumulator),
    AND_ABX to AbsoluteXReadPipeline(AndWithAccumulator),
    AND_ABY to AbsoluteYReadPipeline(AndWithAccumulator),
    AND_IIX to IndexedIndirectReadPipeline(AndWithAccumulator),
    AND_IIY to IndirectIndexedReadPipeline(AndWithAccumulator),

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
    DEC_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(Decrement)),
    DEC_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(Decrement)),
    DEC_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(Decrement)),
    DEC_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(Decrement)),
    DEX to SingleEffectPipeline(DecrementX),
    DEY to SingleEffectPipeline(DecrementY),

    //Exclusive Or
    EOR_I to ImmediateReadPipeline(ExclusiveOr),
    EOR_AB to AbsoluteReadPipeline(ExclusiveOr),
    EOR_ABX to AbsoluteXReadPipeline(ExclusiveOr),
    EOR_ABY to AbsoluteYReadPipeline(ExclusiveOr),
    EOR_Z to ZeroPageReadPipeline(ExclusiveOr),
    EOR_ZX to ZeroPageXReadPipeline(ExclusiveOr),
    EOR_IIX to IndexedIndirectReadPipeline(ExclusiveOr),
    EOR_IIY to IndirectIndexedReadPipeline(ExclusiveOr),

    //Increment
    INC_AB to AbsoluteLocationPipeline(*surroundWithMemoryReadWrite(Increment)),
    INC_ABX to AbsoluteXLocationPipeline(*surroundWithMemoryReadWrite(Increment)),
    INC_Z to ZeroPageLocationPipeline(*surroundWithMemoryReadWrite(Increment)),
    INC_ZX to ZeroPageXLocationPipeline(*surroundWithMemoryReadWrite(Increment)),
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
    ORA_IIX to IndexedIndirectReadPipeline(OrWithAccumulator),
    ORA_IIY to IndirectIndexedReadPipeline(OrWithAccumulator),

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

    //SubtractWithCarry
    SBC_I to ImmediateReadPipeline(SubtractWithCarry),
    SBC_AB to AbsoluteReadPipeline(SubtractWithCarry),
    SBC_ABX to AbsoluteXReadPipeline(SubtractWithCarry),
    SBC_ABY to AbsoluteYReadPipeline(SubtractWithCarry),
    SBC_Z to ZeroPageReadPipeline(SubtractWithCarry),
    SBC_ZX to ZeroPageXReadPipeline(SubtractWithCarry),
    SBC_IIX to IndexedIndirectReadPipeline(SubtractWithCarry),
    SBC_IIY to IndirectIndexedReadPipeline(SubtractWithCarry),

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

val unofficialInstructionList: Array<Pair<UByte, EffectPipeline>> = arrayOf(
    // And + ASL
    ANC_I_UN_0B to ImmediateReadPipeline(AndWithCarry),
    ANC_I_UN_2B to ImmediateReadPipeline(AndWithCarry),

    // AND + LSR
    ASR_I_UN to ImmediateReadPipeline(AndShiftRight),

    //Double No Operation
    DOP_I_UN_80 to ImmediateReadPipeline(DoubleNoOperation),
    DOP_I_UN_82 to ImmediateReadPipeline(DoubleNoOperation),
    DOP_I_UN_89 to ImmediateReadPipeline(DoubleNoOperation),
    DOP_I_UN_C2 to ImmediateReadPipeline(DoubleNoOperation),
    DOP_I_UN_E2 to ImmediateReadPipeline(DoubleNoOperation),
    DOP_Z_UN_04 to ZeroPageReadPipeline(DoubleNoOperation),
    DOP_Z_UN_44 to ZeroPageReadPipeline(DoubleNoOperation),
    DOP_Z_UN_64 to ZeroPageReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_14 to ZeroPageXReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_34 to ZeroPageXReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_54 to ZeroPageXReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_74 to ZeroPageXReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_D4 to ZeroPageXReadPipeline(DoubleNoOperation),
    DOP_ZX_UN_F4 to ZeroPageXReadPipeline(DoubleNoOperation),

    //No Operation
    NOP_UN_1A to SingleEffectPipeline(NoOperation),
    NOP_UN_3A to SingleEffectPipeline(NoOperation),
    NOP_UN_5A to SingleEffectPipeline(NoOperation),
    NOP_UN_7A to SingleEffectPipeline(NoOperation),
    NOP_UN_DA to SingleEffectPipeline(NoOperation),
    NOP_UN_FA to SingleEffectPipeline(NoOperation),

    //Subtract with Carry
    SBC_I_UN_EB to ImmediateReadPipeline(SubtractWithCarry),
)

val instructionMap: Map<UByte, EffectPipeline> = mapOf(
    *instructionList,
    *unofficialInstructionList
).also {
    if (it.size != instructionList.size + unofficialInstructionList.size) {
        throw Error("instructionMap size incorrect")
    }
}

val nmiInterruptPipeline = VariableLengthPipeline(
    NoOperation,
    PushProgramCounterHigh(0),
    PushProgramCounterLow(0),
    PushProcessorStatus(interruptOverride = false),
    LocationFromInterrupt(0xfffa),
    ReadLocationLow,
    ReadLocationHigh,
    ArgumentsToLocation,
    JumpWithBreak
)

val irqInterruptPipeline = VariableLengthPipeline(
    NoOperation,
    PushProgramCounterHigh(0),
    PushProgramCounterLow(0),
    PushProcessorStatus(interruptOverride = false),
    LocationFromInterrupt(0xfffe),
    ReadLocationLow,
    ReadLocationHigh,
    ArgumentsToLocation,
    JumpWithBreak
)

