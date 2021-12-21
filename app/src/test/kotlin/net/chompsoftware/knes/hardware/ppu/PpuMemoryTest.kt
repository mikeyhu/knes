package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.HEADER_SIZE
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.utilities.nextUByteNotZero
import net.chompsoftware.knes.hardware.utilities.setupMemoryWithNES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

@ExperimentalUnsignedTypes
class PpuMemoryTest {

    @Test
    fun `Maps memory from 0x0000 to 0x2000 into the mapper chr-rom`() {
        val rom = setupMemoryWithNES(
            0x1au,
            0x01u, // prgSize 4000
            0x02u, // chrSize 4000
            size = 0x8000 + HEADER_SIZE
        )

        for (i in 0x4000 until 0x8000) {
            rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
        }
        val mapper = RomLoader.loadMapper(rom)
        val ppuMemory = PpuMemory(mapper)

        for (i in 0x0 until 0x2000) {
            assertTrue(mapper.getChrRom(i) > 0u)
            assertEquals(mapper.getChrRom(i), ppuMemory.get(i))
        }
    }

    @Test
    fun `Maps memory from 0x0000 to 0x2000 into the chr-rom and allows access to a tile`() {
        val rom = setupMemoryWithNES(
            0x1au,
            0x01u, // prgSize 4000
            0x02u, // chrSize 4000
            size = 0x8000 + HEADER_SIZE
        )

        for (i in 0x4000 until 0x8000) {
            rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
        }
        val mapper = RomLoader.loadMapper(rom)
        val ppuMemory = PpuMemory(mapper)

        val expectedTile = mapper.getChrRomSlice(0, 0x10)
        val actualTile = ppuMemory.getSlice(0, 0x10)

        for (i in 0x0 until 0x10) {
            assertTrue(mapper.getChrRom(i) > 0u)
            assertEquals(expectedTile[i], actualTile[i])
        }
    }

    @Test
    fun `Does not allow getting a tile outside the range`() {
        val rom = setupMemoryWithNES(
            0x1au,
            0x01u, // prgSize 4000
            0x02u, // chrSize 4000
            size = 0x8000 + HEADER_SIZE
        )

        val mapper = RomLoader.loadMapper(rom)
        val ppuMemory = PpuMemory(mapper)

        assertThrows<Error> {
            ppuMemory.getSlice(0x2000, 0x10)
        }
    }
}