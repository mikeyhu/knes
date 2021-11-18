package net.chompsoftware.knes.hardware

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NesMemoryTest {
    @Test
    fun `Has 2KB of RAM mirrored 3 times`() {
        val memory = NesMemory()

        for (i in 0..0x1fff) {
            assertEquals(0u, memory[i].toUInt(), "Memory should be zeroed out at $i")
        }

        for (i in 0 until 0x800) {
            val byte = Random.nextInt(0x1, 0xff).toUByte()
            memory[i] = byte
            assertEquals(byte, memory[i], "Memory $i incorrect")
            assertEquals(byte, memory[0x800 + i], "Memory $i mirror 2 incorrect")
            assertEquals(byte, memory[0x1000 + i], "Memory $i mirror 3 incorrect")
            assertEquals(byte, memory[0x1800 + i], "Memory $i mirror 4 incorrect")
        }

        for (i in 0..0x1fff) {
            assertNotEquals(0u, memory[i].toUInt(), "Memory $i was not set")
        }
    }

    @Test
    fun `Has PPU Registered mirrored from 0x2000 until 0x3fff`() {
        val memory = NesMemory()

        for (i in 0x2000..0x3fff) {
            assertEquals(0u, memory[i].toUInt(), "PPU should be zeroed out at $i")
        }

        for (i in 0 until 0x8) {
            val byte = Random.nextInt(0x1, 0xff).toUByte()
            memory[0x2000 + i] = byte
            for (k in 0x2008 until 0x3ff8 step 8) {
                assertEquals(byte, memory[k + i], "PPU $i at $k incorrect")
            }
        }

        for (i in 0..0x1fff) {
            assertEquals(0u, memory[i].toUInt(), "Memory $i should not have been set")
        }

        for (i in 0x2000..0x3fff) {
            assertNotEquals(0u, memory[i].toUInt(), "Memory $i was not set")
        }
    }
}