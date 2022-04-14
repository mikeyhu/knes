package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.maskedEquals

fun UByteArray.spriteYPosition(sprite: Int) = this[sprite * 4]
fun UByteArray.spriteAttributes(sprite: Int): SpriteAttribute = this[sprite * 4 + 2]
fun UByteArray.spriteIndexNumber(sprite: Int) = this[sprite * 4 + 1]
fun UByteArray.spriteXPosition(sprite: Int) = this[sprite * 4 + 3]

typealias SpriteAttribute = UByte

fun SpriteAttribute.spritePalette() = this.and(0x3u).toInt()
fun SpriteAttribute.spritePriority() = this.maskedEquals(0x20u)
fun SpriteAttribute.spriteFlipHorizontal() = this.maskedEquals(0x40u)
fun SpriteAttribute.spriteFlipVertical() = this.maskedEquals(0x80u)
