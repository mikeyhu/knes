package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class PpuOperationStateTest {

    @ParameterizedTest
    @CsvSource(
        "0x00,0x2000,0x0000",
        "0x01,0x2400,0x0400",
        "0x02,0x2800,0x0800",
        "0x03,0x2C00,0x0C00",
        "0x04,0x2000,0x0000"
    )
    fun `Will set baseNameTableAddress correctly and can get the offset only`(
        input: Int,
        baseNameTableAddress: Int,
        baseNameTableOffset: Int
    ) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(baseNameTableAddress, operationState.baseNametableAddress)
        assertEquals(baseNameTableOffset, operationState.getBaseNameTableOffset())
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,01",
        "0x04,32"
    )
    fun `Will set vRamAddressIncrement correctly`(input: Int, vRamAddressIncrement: Int) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(vRamAddressIncrement, operationState.vRamAddressIncrement)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,0x0000",
        "0x08,0x1000"
    )
    fun `Will set spritePatternAddress correctly`(input: Int, spritePatternAddress: Int) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(spritePatternAddress, operationState.spritePatternAddress)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,0x0000",
        "0x10,0x1000"
    )
    fun `Will set backgroundPatternAddress correctly`(input: Int, backgroundPatternAddress: Int) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(backgroundPatternAddress, operationState.backgroundPatternAddress)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,Sprite8x8",
        "0x20,Sprite8x16"
    )
    fun `Will set spriteSize correctly`(input: Int, spriteSizeName: String) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(
            PpuOperationState.SpriteSize.valueOf(spriteSizeName),
            operationState.spriteSize
        )
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,ReadBackdrop",
        "0x40,OutputColour"
    )
    fun `Will set extPinSelect correctly`(input: Int, extPinSelect: String) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(
            PpuOperationState.ExtPinSelect.valueOf(extPinSelect),
            operationState.extPinSelect
        )
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,false",
        "0x80,true"
    )
    fun `Will set generateNMIOnInterval correctly`(input: Int, generateNMIOnInterval: Boolean) {
        val operationState = PpuOperationState.fromUByte(input.toUByte())
        assertEquals(generateNMIOnInterval, operationState.generateNMIOnInterval)
    }

    @Test
    fun `Will set everything correctly`() {
        val operationState = PpuOperationState.fromUByte(0xffu)

        operationState.apply {
            assertEquals(0x2C00, baseNametableAddress)
            assertEquals(32, vRamAddressIncrement)
            assertEquals(0x1000, spritePatternAddress)
            assertEquals(0x1000, backgroundPatternAddress)
            assertEquals(PpuOperationState.SpriteSize.Sprite8x16, spriteSize)
            assertEquals(PpuOperationState.ExtPinSelect.OutputColour, extPinSelect)
            assertEquals(true, generateNMIOnInterval)
        }
    }
}