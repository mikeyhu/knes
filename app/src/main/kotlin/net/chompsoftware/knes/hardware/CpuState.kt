package net.chompsoftware.knes.hardware

data class CpuState(
    var programCounter: Int = 0,
    var aReg: UByte = 0x0u,
    var xReg: UByte = 0x0u,
    var yReg: UByte = 0x0u,
    var stackReg: UByte = 0x0u,
    var isNegativeFlag: Boolean = false,
    var isZeroFlag: Boolean = false,
    var isCarryFlag: Boolean = false,
    var isDecimalFlag: Boolean = false,
    var isOverflowFlag: Boolean = false
) {
    fun programCounterWithIncrement(): Int {
        return programCounter++
    }

    fun setARegWithFlags(value: UByte) {
        aReg = value
        isNegativeFlag = tweakNegative(value)
        isZeroFlag = tweakZero(value)
    }

    fun setXRegWithFlags(value: UByte) {
        xReg = value
        isNegativeFlag = tweakNegative(value)
        isZeroFlag = tweakZero(value)
    }

    fun setYRegWithFlags(value: UByte) {
        yReg = value
        isNegativeFlag = tweakNegative(value)
        isZeroFlag = tweakZero(value)
    }

    fun setComparisonFlags(existing: UByte, compareTo: UByte) {
        isZeroFlag = existing == compareTo
        isNegativeFlag = existing < compareTo
        isCarryFlag = existing >= compareTo
    }

    private fun tweakNegative(value: UByte) = value.and(NEGATIVE_BYTE_POSITION) > 0u
    private fun tweakZero(value: UByte) = value == ZERO_BYTE
}

const val NEGATIVE_BYTE_POSITION: UByte = 0x80u
const val ZERO_BYTE: UByte = 0x0u



