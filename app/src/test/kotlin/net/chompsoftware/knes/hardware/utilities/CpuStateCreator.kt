package net.chompsoftware.knes.hardware.utilities

import net.chompsoftware.knes.hardware.CpuState
import kotlin.random.Random


fun randomisedCpuState(
    programCounter: Int = 0,
    aReg: UByte? = null,
    xReg: UByte? = null,
    yReg: UByte? = null,
    stackReg: UByte? = null,
    breakLocation: Int? = null,
    isNegativeFlag: Boolean? = null,
    isZeroFlag: Boolean? = null,
    isCarryFlag: Boolean? = null,
    isDecimalFlag: Boolean? = null,
    isOverflowFlag: Boolean? = null,
    isBreakCommandFlag: Boolean? = null,
    isInterruptDisabledFlag: Boolean? = null
): CpuState {
    return CpuState(
        programCounter = programCounter,
        aReg = aReg ?: randomUByte(),
        xReg = xReg ?: randomUByte(),
        yReg = yReg ?: randomUByte(),
        stackReg = stackReg ?: randomUByte(),
        breakLocation = breakLocation ?: randomInt16(),
        isNegativeFlag = isNegativeFlag ?: randomBoolean(),
        isZeroFlag = isZeroFlag ?: randomBoolean(),
        isCarryFlag = isCarryFlag ?: randomBoolean(),
        isDecimalFlag = isDecimalFlag ?: randomBoolean(),
        isOverflowFlag = isOverflowFlag ?: randomBoolean(),
        isBreakCommandFlag = isBreakCommandFlag ?: randomBoolean(),
        isInterruptDisabledFlag = isInterruptDisabledFlag ?: randomBoolean()
    )
}

private fun randomUByte() = Random.nextInt(0x0, 0xff).toUByte()
private fun randomInt16() = Random.nextInt(0x0, 0xffff)
private fun randomBoolean() = Random.nextBoolean()