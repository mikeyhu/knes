package net.chompsoftware.knes.hardware

data class CpuState(
    var programCounter: Int = 0,
    var aReg: UInt = 0x0u,
    var xReg: UInt = 0x0u,
    var isNegativeFlag: Boolean = false,
    var isZeroFlag: Boolean = false,
    var isCarryFlag: Boolean = false
) {
    fun programCounterWithIncrement(): Int {
        return programCounter++
    }

    fun setARegWithFlags(value:UByte) {
        val asUInt = value.toUInt()
        aReg = asUInt
        isNegativeFlag = tweakNegative(asUInt)
        isZeroFlag = tweakZero(asUInt)
    }

    fun setXRegWithFlags(value:UByte) {
        val asUInt = value.toUInt()
        xReg = asUInt
        isNegativeFlag = tweakNegative(asUInt)
        isZeroFlag = tweakZero(asUInt)
    }

    fun getAReg() : UByte {
        return aReg.toUByte()
    }

    fun getXReg() : UByte {
        return xReg.toUByte()
    }

    fun setComparisonFlags(existing:UByte, compareTo:UByte) {
        isZeroFlag = existing == compareTo
        isNegativeFlag = existing < compareTo
        isCarryFlag = existing >= compareTo
    }

    private fun tweakNegative(value: UInt) = value.and(NEGATIVE_BYTE_POSITION) > 0u
    private fun tweakZero(value: UInt) = value == 0u
}

const val NEGATIVE_BYTE_POSITION = 0x80u



