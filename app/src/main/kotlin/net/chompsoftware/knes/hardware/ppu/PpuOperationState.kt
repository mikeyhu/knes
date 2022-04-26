package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.maskedEquals
import net.chompsoftware.knes.paddedToHex


data class PpuOperationState(
    val baseNametableAddress: Int,
    val vRamAddressIncrement: Int,
    val spritePatternAddress: Int,
    val backgroundPatternAddress: Int,
    val spriteSize: SpriteSize,
    val extPinSelect: ExtPinSelect,
    val generateNMIOnInterval: Boolean

) {
    enum class SpriteSize(val vertical: Int, val horizontal: Int) {
        Sprite8x8(8, 8),
        Sprite8x16(8, 16)
    }

    enum class ExtPinSelect {
        ReadBackdrop,
        OutputColour
    }

    override fun toString(): String {
        return "PpuOperationState(baseNametable=${baseNametableAddress.paddedToHex()}, " +
                "vRamInc=${vRamAddressIncrement.paddedToHex()}, " +
                "spritePat=${spritePatternAddress.paddedToHex()}, " +
                "backgroundPat=${backgroundPatternAddress.paddedToHex()}, " +
                "spriteSize=$spriteSize, " +
                "extPin=$extPinSelect, " +
                "genNMI=$generateNMIOnInterval)"
    }

    fun getBaseNameTableOffset() = baseNametableAddress - 0x2000

    companion object {
        private const val BASE_NAME_TABLE_ADDRESS_MASK = 0x3
        private const val VRAM_ADDRESS_INCREMENT_POSITION: UByte = 0x4u
        private const val SPRITE_PATTERN_ADDRESS_POSITION: UByte = 0x8u
        private const val BACKGROUND_PATTERN_ADDRESS_POSITION: UByte = 0x10u
        private const val SPRITE_SIZE_POSITION: UByte = 0x20u
        private const val EXT_PIN_SELECT_POSITION: UByte = 0x40u
        private const val GENERATE_NMI_ON_INTERVAL_POSITION: UByte = 0x80u

        fun fromUByte(input: UByte): PpuOperationState {
            return PpuOperationState(
                baseNameTableAddress(input),
                vRamAddressIncrement(input),
                spritePatternAddress(input),
                backgroundPatternAddress(input),
                spriteSize(input),
                extPinSelect(input),
                generateNMIOnInterval(input)
            )
        }

        private fun baseNameTableAddress(input: UByte) =
            0x2000 + (input.toInt().and(BASE_NAME_TABLE_ADDRESS_MASK) * 0x400)

        private fun vRamAddressIncrement(input: UByte) =
            if (input.maskedEquals(VRAM_ADDRESS_INCREMENT_POSITION)) 32 else 1

        private fun spritePatternAddress(input: UByte) =
            if (input.maskedEquals(SPRITE_PATTERN_ADDRESS_POSITION)) 0x1000 else 0

        private fun backgroundPatternAddress(input: UByte) =
            if (input.maskedEquals(BACKGROUND_PATTERN_ADDRESS_POSITION)) 0x1000 else 0

        private fun spriteSize(input: UByte) =
            if (input.maskedEquals(SPRITE_SIZE_POSITION)) SpriteSize.Sprite8x16 else SpriteSize.Sprite8x8

        private fun extPinSelect(input: UByte) =
            if (input.maskedEquals(EXT_PIN_SELECT_POSITION)) ExtPinSelect.OutputColour else ExtPinSelect.ReadBackdrop

        private fun generateNMIOnInterval(input: UByte) = input.maskedEquals(GENERATE_NMI_ON_INTERVAL_POSITION)
    }
}

