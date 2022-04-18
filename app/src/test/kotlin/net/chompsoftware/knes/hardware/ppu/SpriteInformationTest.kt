package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class SpriteInformationTest {

    @Test
    fun `Can retrieve Y position for sprites`() {
        val bytes = UByteArray(0x100) {
            it.toUByte()
        }

        for (i in 0 until 64 step 4) {
            assertEquals((i * 4), bytes.spriteYPosition(i))
        }
    }

    @Test
    fun `Can retrieve IndexNumber for sprites`() {
        val bytes = UByteArray(0x100) {
            it.toUByte()
        }

        for (i in 0 until 64 step 4) {
            assertEquals((i * 4 + 1), bytes.spriteIndexNumber(i))
        }
    }

    @Test
    fun `Can retrieve Sprite Attributes for sprites`() {
        val bytes = UByteArray(0x100) {
            it.toUByte()
        }

        for (i in 0 until 64 step 4) {
            assertEquals((i * 4 + 2).toUByte(), bytes.spriteAttributes(i))
        }
    }

    @Test
    fun `Can retrieve X position for sprites`() {
        val bytes = UByteArray(0x100) {
            it.toUByte()
        }

        for (i in 0 until 64 step 4) {
            assertEquals((i * 4 + 3), bytes.spriteXPosition(i))
        }
    }

    @Test
    fun `SpriteAttribute Palette definition`() {
        assertEquals(0, 0.toUByte().spritePalette())
        assertEquals(1, 1.toUByte().spritePalette())
        assertEquals(2, 2.toUByte().spritePalette())
        assertEquals(3, 3.toUByte().spritePalette())
        assertEquals(0, 4.toUByte().spritePalette())
        assertEquals(1, 5.toUByte().spritePalette())
    }

    @Test
    fun `SpriteAttribute Priority definition`() {
        assertFalse(0x0.toUByte().spritePriority())
        assertFalse(0x10.toUByte().spritePriority())
        assertTrue(0x20.toUByte().spritePriority())
        assertTrue(0x30.toUByte().spritePriority())
        assertFalse(0x40.toUByte().spritePriority())
        assertTrue(0x60.toUByte().spritePriority())
    }

    @Test
    fun `SpriteAttribute Horizontal flip definition`() {
        assertFalse(0x0.toUByte().spriteFlipHorizontal())
        assertFalse(0x10.toUByte().spriteFlipHorizontal())
        assertTrue(0x40.toUByte().spriteFlipHorizontal())
        assertTrue(0x60.toUByte().spriteFlipHorizontal())
        assertFalse(0x80.toUByte().spriteFlipHorizontal())
    }

    @Test
    fun `SpriteAttribute Vertical flip definition`() {
        assertFalse(0x0.toUByte().spriteFlipVertical())
        assertFalse(0x10.toUByte().spriteFlipVertical())
        assertTrue(0x80.toUByte().spriteFlipVertical())
    }
}