package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.rom.HEADER_SIZE
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.hardware.utilities.nextUByteNotZero
import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NesMemoryTest {

    inner class FakeRomMapper : RomMapper {
        override fun getPrgRom(position: Int): UByte {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun getBatteryBackedRam(position: Int): UByte {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun setBatteryBackedRam(position: Int, value: UByte) {
            throw Error("Interacting with FakeRomMapper")
        }
    }

    @Test
    fun `Has 2KB of RAM mirrored 3 times`() {
        val memory = NesMemory(FakeRomMapper())

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
        val memory = NesMemory(FakeRomMapper())

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

    @Test
    fun `Maps memory above 0x8000 into the mapper`() {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x02u, // prgSize 8000
            size = 0x8000 + HEADER_SIZE
        )

        for (i in 0 until 0x8000) {
            rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
        }
        val mapper = RomLoader.loadMapper(rom)
        val memory = NesMemory(mapper)

        for (i in 0x8000 until 0x10000) {
            assertEquals(mapper.getPrgRom(i), memory[i])
        }
    }
}