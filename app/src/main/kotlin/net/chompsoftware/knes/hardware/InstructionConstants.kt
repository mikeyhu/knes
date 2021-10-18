package net.chompsoftware.knes.hardware

//Add With Carry
const val ADC_I: UByte = 0x69u

//Branch
const val BCC: UByte = 0x90u //On carry clear
const val BCS: UByte = 0xb0u //On carry set
const val BEQ: UByte = 0xf0u //On equal
const val BMI: UByte = 0x30u //On minus
const val BNE: UByte = 0xd0u //On not equal
const val BPL: UByte = 0x10u //On plus
const val BVC: UByte = 0x50u //On overflow clear
const val BVS: UByte = 0x70u //On overflow set

//Clear
const val CLC: UByte = 0x18u //Carry
const val CLD: UByte = 0xd8u //Decimal
const val CLV: UByte = 0xb8u //Overflow

//Compare
const val CMP_I: UByte = 0xc9u //Memory with Accumulator
const val CMP_AB: UByte = 0xcdu

const val CPX_I: UByte = 0xe0u //Memory with X

const val CPY_I: UByte = 0xc0u //Memory with Y

//Decrement
const val DEX: UByte = 0xcau //X
const val DEY: UByte = 0x88u //Y

//Exclusive OR
const val EOR_I: UByte = 0x49u

//Increment
const val INX: UByte = 0xe8u

//Jump
const val JMP_AB: UByte = 0x4cu

//Load Accumulator
const val LDA_I: UByte = 0xa9u
const val LDA_AB: UByte = 0xadu
const val LDA_Z: UByte = 0xa5u

//Load X
const val LDX_I: UByte = 0xa2u
const val LDX_AB: UByte = 0xaeu

//Load Y
const val LDY_I: UByte = 0xa0u
const val LDY_AB: UByte = 0xacu

//No Operation
const val NOP: UByte = 0xeau

//Push and Pull Stack Operations
const val PHA: UByte = 0x48u // Push Accumulator
const val PLA: UByte = 0x68u // Pull Accumulator
const val PLP: UByte = 0x28u // Pull Processor Status

//Store
const val STA_Z: UByte = 0x85u //Accumulator in memory
const val STA_AB: UByte = 0x8du

//Transfer
const val TAX: UByte = 0xaau //Accumulator to X
const val TAY: UByte = 0xa8u //Accumulator to Y
const val TSX: UByte = 0xbau //Stack to X
const val TXA: UByte = 0x8au //X to Accumulator
const val TXS: UByte = 0x9au //X to Stack Register
const val TYA: UByte = 0x98u //Y to Accumulator











