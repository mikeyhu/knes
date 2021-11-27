package net.chompsoftware.knes.hardware.utilities

import kotlin.random.Random


fun Random.nextUByteNotZero() = this.nextInt(0x1, 0xff).toUByte()
fun Random.nextUByte() = this.nextInt(0x0, 0xff).toUByte()

