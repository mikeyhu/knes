package net.chompsoftware.knes.hardware

//Clear
const val CLD: UByte = 0xd8u //Decimal


//Branch
const val BEQ: UByte = 0xf0u //On equal
const val BNE: UByte = 0xd0u //On not equal

//Compare
const val CMP_I: UByte = 0xc9u //Memory with Accumulator

//Increment
const val INX: UByte = 0xe8u

//Load Accumulator
const val LDA_I: UByte = 0xa9u
const val LDA_AB: UByte = 0xadu
const val LDA_Z: UByte = 0xa5u

//Load X
const val LDX_I: UByte = 0xa2u
const val LDX_AB: UByte = 0xaeu

//No Operation
const val NOP: UByte = 0xeau

//Store
const val STA_Z: UByte = 0x85u //Accumulator in memory

//Transfer
const val TAX: UByte = 0xaau //Accumulator to X
const val TXA: UByte = 0x8au //X to Accumulator










