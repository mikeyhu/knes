package net.chompsoftware.knes.hardware.utilities

import net.chompsoftware.knes.setupMemory
import kotlin.random.Random


fun Random.nextUByteNotZero() = this.nextInt(0x1, 0xff).toUByte()
fun Random.nextUByte() = this.nextInt(0x0, 0xff).toUByte()


fun setupMemoryWithNES(vararg bytes: UByte, size: Int = 0x8000, memoryOffset: Int = 0) = setupMemory(
    'N'.code.toUByte(),
    'E'.code.toUByte(),
    'S'.code.toUByte(),
    *bytes,
    size = size,
    memoryOffset = memoryOffset
)