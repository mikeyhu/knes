package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.isNegative
import net.chompsoftware.knes.isZero
import net.chompsoftware.knes.toHex

data class CpuState(
    var programCounter: Int = 0,
    var aReg: UByte = 0x0u,
    var xReg: UByte = 0x0u,
    var yReg: UByte = 0x0u,
    var stackReg: UByte = 0x0u,
    var breakLocation: Int = 0,
    var isNegativeFlag: Boolean = false,
    var isZeroFlag: Boolean = false,
    var isCarryFlag: Boolean = false,
    var isDecimalFlag: Boolean = false,
    var isOverflowFlag: Boolean = false,
    var isBreakCommandFlag: Boolean = false,
    var isInterruptDisabledFlag: Boolean = false
) {
    fun programCounterWithIncrement(): Int {
        return programCounter++
    }

    fun setARegWithFlags(value: UByte) {
        aReg = value
        isNegativeFlag = value.isNegative()
        isZeroFlag = value.isZero()
    }

    fun setXRegWithFlags(value: UByte) {
        xReg = value
        isNegativeFlag = value.isNegative()
        isZeroFlag = value.isZero()
    }

    fun setYRegWithFlags(value: UByte) {
        yReg = value
        isNegativeFlag = value.isNegative()
        isZeroFlag = value.isZero()
    }

    fun setNegativeZeroFlags(value: UByte) {
        isNegativeFlag = value.isNegative()
        isZeroFlag = value.isZero()
    }

    fun setComparisonFlags(existing: UByte, compareTo: UByte) {
        isZeroFlag = existing == compareTo
        isNegativeFlag = (existing - compareTo).toUByte().isNegative()
        isCarryFlag = existing >= compareTo
    }

    override fun toString(): String {
        return "CpuState(pc=${programCounter.paddedToHex()}, " +
                "aReg=${aReg.paddedToHex()}, xReg=${xReg.paddedToHex()}, " +
                "yReg=${yReg.paddedToHex()}, stackReg=${stackReg.paddedToHex()}, " +
                "negative=${isNegativeFlag}, zero=${isZeroFlag}, " +
                "overflow=${isOverflowFlag}, carry=${isCarryFlag}, " +
                "decimal=${isDecimalFlag}, break=${isBreakCommandFlag}, " +
                "interrupt=${isInterruptDisabledFlag})"
    }

    private fun Int.paddedToHex() = this.toHex().padEnd(5)

    private fun UByte.paddedToHex() = this.toHex().padEnd(4)
}




