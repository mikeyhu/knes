package net.chompsoftware.knes.hardware.rom

import net.chompsoftware.knes.hardware.utilities.nextUByteNotZero
import net.chompsoftware.knes.hardware.utilities.setupMemoryWithNES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random


class RomMapperTest {
    @Nested
    inner class TypeZeroRomMapperTest {
        @Test
        fun `Throws Error if memory outside ROM range is requested`() {
            val rom = setupMemoryWithNES(
                0x1au,
                0x02u,
                size = 0x8000 + HEADER_SIZE
            )


            val mapper = RomLoader.loadMapper(rom)
            for (i in 0 until 0x8000) {
                assertThrows<RomMapperError> { mapper.getPrgRom(i) }
            }
            assertThrows<RomMapperError> { mapper.getPrgRom(0x10000) }
        }

        @Test
        fun `Loads a 32K ROM`() {
            val rom = setupMemoryWithNES(
                0x1au,
                0x02u, // prgSize 8000
                size = 0x8000 + HEADER_SIZE
            )

            for (i in 0 until 0x8000) {
                rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
            }

            val mapper = RomLoader.loadMapper(rom)
            for (i in 0 until 0x8000) {
                assertEquals(rom[i + HEADER_SIZE], mapper.getPrgRom(i + 0x8000))
            }
        }

        @Test
        fun `Loads a 16K ROM`() {
            val rom = setupMemoryWithNES(
                0x1au,
                0x01u, // prgSize 4000
                size = 0x4000 + HEADER_SIZE
            )

            for (i in 0 until 0x4000) {
                rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
            }

            val mapper = RomLoader.loadMapper(rom)
            for (i in 0 until 0x4000) {
                assertEquals(rom[i + HEADER_SIZE], mapper.getPrgRom(i + 0x8000))
                assertEquals(rom[i + HEADER_SIZE], mapper.getPrgRom(i + 0xC000))
            }
        }

        @Test
        fun `Allows batteryBackedRam`() {
            val rom = setupMemoryWithNES(
                0x1au,
                0x01u, // prgSize 4000
                0x00u,
                0x02u, // battery backup ram
                size = 0x4000 + HEADER_SIZE
            )

            val mapper = RomLoader.loadMapper(rom)

            for (i in 0x6000 until 0x8000) {
                val expected = Random.nextUByteNotZero()
                mapper.setBatteryBackedRam(i, expected)
                assertEquals(expected, mapper.getBatteryBackedRam(i))
            }
        }

        @Test
        fun `Loads a ROM with both PrgRom and ChrRom`() {
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
            for (i in 0 until 0x2000) {
                assertEquals(rom[i + 0x4000 + HEADER_SIZE], mapper.getChrRom(i))
            }
        }

        @Test
        fun `Loads ChrRom and can get a slice of it`() {
            val rom = setupMemoryWithNES(
                0x1au,
                0x01u, // prgSize 4000
                0x02u, // chrSize 4000
                size = 0x8000 + HEADER_SIZE
            )

            for (i in 0x4000 until 0x4010) {
                rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
            }

            val mapper = RomLoader.loadMapper(rom)

            val sprite = mapper.getChrRomSlice(0, 16)

            assertEquals(16, sprite.size)

            for (i in 0 until 16) {
                assertEquals(rom[i + 0x4000 + HEADER_SIZE], sprite[i])
            }
        }
    }
}