package net.chompsoftware.knes.hardware

//Add With Carry
const val ADC_I: UByte = 0x69u
const val ADC_AB: UByte = 0x6du
const val ADC_ABX: UByte = 0x7du
const val ADC_ABY: UByte = 0x79u
const val ADC_Z: UByte = 0x65u
const val ADC_ZX: UByte = 0x75u
const val ADC_IIX: UByte = 0x61u
const val ADC_IIY: UByte = 0x71u

// And With Accumulator
const val AND_I: UByte = 0x29u
const val AND_Z: UByte = 0x25u
const val AND_ZX: UByte = 0x35u
const val AND_AB: UByte = 0x2du
const val AND_ABX: UByte = 0x3du
const val AND_ABY: UByte = 0x39u
const val AND_IIX: UByte = 0x21u
const val AND_IIY: UByte = 0x31u

// Arithmetic Shift Left
const val ASL_NONE: UByte = 0x0au
const val ASL_AB: UByte = 0x0eu
const val ASL_ABX: UByte = 0x1eu
const val ASL_Z: UByte = 0x06u
const val ASL_ZX: UByte = 0x16u

// Bit
const val BIT_Z: UByte = 0x24u
const val BIT_AB: UByte = 0x2cu

//Branch
const val BCC: UByte = 0x90u //On carry clear
const val BCS: UByte = 0xb0u //On carry set
const val BEQ: UByte = 0xf0u //On equal
const val BMI: UByte = 0x30u //On minus
const val BNE: UByte = 0xd0u //On not equal
const val BPL: UByte = 0x10u //On plus
const val BVC: UByte = 0x50u //On overflow clear
const val BVS: UByte = 0x70u //On overflow set

//Break
const val BRK: UByte = 0x00u

//Clear
const val CLC: UByte = 0x18u //Carry
const val CLD: UByte = 0xd8u //Decimal
const val CLV: UByte = 0xb8u //Overflow
const val CLI: UByte = 0x58u //Interrupt

//Compare
const val CMP_I: UByte = 0xc9u //Memory with Accumulator
const val CMP_AB: UByte = 0xcdu
const val CMP_ABX: UByte = 0xddu
const val CMP_ABY: UByte = 0xd9u
const val CMP_Z: UByte = 0xc5u
const val CMP_ZX: UByte = 0xd5u
const val CMP_IIX: UByte = 0xc1u
const val CMP_IIY: UByte = 0xd1u

const val CPX_I: UByte = 0xe0u //Memory with X
const val CPX_AB: UByte = 0xecu //Memory with X
const val CPX_Z: UByte = 0xe4u //Memory with X

const val CPY_I: UByte = 0xc0u //Memory with Y
const val CPY_AB: UByte = 0xccu //Memory with Y
const val CPY_Z: UByte = 0xc4u //Memory with Y

//Decrement
const val DEC_AB: UByte = 0xceu
const val DEC_ABX: UByte = 0xdeu
const val DEC_Z: UByte = 0xc6u
const val DEC_ZX: UByte = 0xd6u

const val DEX: UByte = 0xcau //X
const val DEY: UByte = 0x88u //Y

//Exclusive OR
const val EOR_I: UByte = 0x49u
const val EOR_AB: UByte = 0x4du
const val EOR_ABX: UByte = 0x5du
const val EOR_ABY: UByte = 0x59u
const val EOR_Z: UByte = 0x45u
const val EOR_ZX: UByte = 0x55u
const val EOR_IIX: UByte = 0x41u
const val EOR_IIY: UByte = 0x51u

//Increment
const val INC_Z: UByte = 0xe6u
const val INC_ZX: UByte = 0xf6u
const val INC_AB: UByte = 0xeeu
const val INC_ABX: UByte = 0xfeu
const val INX: UByte = 0xe8u
const val INY: UByte = 0xc8u

//Jump
const val JMP_AB: UByte = 0x4cu
const val JMP_IN: UByte = 0x6cu
const val JSR_AB: UByte = 0x20u

//Load Accumulator
const val LDA_I: UByte = 0xa9u
const val LDA_Z: UByte = 0xa5u
const val LDA_ZX: UByte = 0xb5u
const val LDA_AB: UByte = 0xadu
const val LDA_ABX: UByte = 0xbdu
const val LDA_ABY: UByte = 0xb9u
const val LDA_IIX: UByte = 0xa1u
const val LDA_IIY: UByte = 0xb1u

//Load X
const val LDX_I: UByte = 0xa2u
const val LDX_AB: UByte = 0xaeu
const val LDX_ABY: UByte = 0xbeu
const val LDX_Z: UByte = 0xa6u
const val LDX_ZY: UByte = 0xb6u

//Load Y
const val LDY_I: UByte = 0xa0u
const val LDY_AB: UByte = 0xacu
const val LDY_ABX: UByte = 0xbcu
const val LDY_Z: UByte = 0xa4u
const val LDY_ZX: UByte = 0xb4u

// Logical Shift Right
const val LSR_NONE: UByte = 0x4au
const val LSR_Z: UByte = 0x46u
const val LSR_ZX: UByte = 0x56u
const val LSR_AB: UByte = 0x4eu
const val LSR_ABX: UByte = 0x5eu

//No Operation
const val NOP: UByte = 0xeau

//Or with Accumulator
const val ORA_I: UByte = 0x09u
const val ORA_Z: UByte = 0x05u
const val ORA_ZX: UByte = 0x15u
const val ORA_AB: UByte = 0x0Du
const val ORA_ABX: UByte = 0x1Du
const val ORA_ABY: UByte = 0x19u
const val ORA_IIX: UByte = 0x01u
const val ORA_IIY: UByte = 0x11u

// Rotate Left
const val ROL_NONE: UByte = 0x2au
const val ROL_Z: UByte = 0x26u
const val ROL_ZX: UByte = 0x36u
const val ROL_AB: UByte = 0x2eu
const val ROL_ABX: UByte = 0x3eu

// Rotate Right
const val ROR_NONE: UByte = 0x6au
const val ROR_Z: UByte = 0x66u
const val ROR_ZX: UByte = 0x76u
const val ROR_AB: UByte = 0x6eu
const val ROR_ABX: UByte = 0x7eu

//Push and Pull Stack Operations
const val PHA: UByte = 0x48u // Push Accumulator
const val PHP: UByte = 0x08u // Push Processor Status
const val PLA: UByte = 0x68u // Pull Accumulator
const val PLP: UByte = 0x28u // Pull Processor Status

//Return
const val RTI: UByte = 0x40u //from break
const val RTS: UByte = 0x60u //from subroutine

// Subtract With Carry
const val SBC_I: UByte = 0xe9u
const val SBC_Z: UByte = 0xe5u
const val SBC_ZX: UByte = 0xf5u
const val SBC_AB: UByte = 0xedu
const val SBC_ABX: UByte = 0xfdu
const val SBC_ABY: UByte = 0xf9u
const val SBC_IIX: UByte = 0xe1u
const val SBC_IIY: UByte = 0xf1u

//Set
const val SEC: UByte = 0x38u //carry
const val SEI: UByte = 0x78u //interrupt
const val SED: UByte = 0xf8u //decimal

//Store
const val STA_Z: UByte = 0x85u //Accumulator in memory
const val STA_ZX: UByte = 0x95u
const val STA_AB: UByte = 0x8du
const val STA_ABX: UByte = 0x9du
const val STA_ABY: UByte = 0x99u
const val STA_IIX: UByte = 0x81u
const val STA_IIY: UByte = 0x91u

const val STX_Z: UByte = 0x86u //X in memory
const val STX_ZY: UByte = 0x96u
const val STX_AB: UByte = 0x8eu

const val STY_Z: UByte = 0x84u //Y in memory
const val STY_ZX: UByte = 0x94u
const val STY_AB: UByte = 0x8cu

//Transfer
const val TAX: UByte = 0xaau //Accumulator to X
const val TAY: UByte = 0xa8u //Accumulator to Y
const val TSX: UByte = 0xbau //Stack to X
const val TXA: UByte = 0x8au //X to Accumulator
const val TXS: UByte = 0x9au //X to Stack Register
const val TYA: UByte = 0x98u //Y to Accumulator











