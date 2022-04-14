package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.hardware.rom.HEADER_SIZE
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.hardware.utilities.nextUByteNotZero
import net.chompsoftware.knes.hardware.utilities.setupMemoryWithNES
import net.chompsoftware.knes.hardware.utilities.ubyteArrayOfSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random


class NesPpuMemoryTest {

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
        val ppuMemory = NesPpuMemory(mapper)

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
        val ppuMemory = NesPpuMemory(mapper)

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
        val ppuMemory = NesPpuMemory(mapper)

        assertThrows<Error> {
            ppuMemory.getSlice(0x2000, 0x10)
        }
    }

    @Test
    fun `OAM DMA writes get written to OAM memory`() {
        val expectedBytes = Random.ubyteArrayOfSize(0x100)

        val ppuMemory = NesPpuMemory(getMapper())

        ppuMemory.oamDmaWrite(expectedBytes, 0)

        for (i in 0 until 0x100) {
            assertEquals(expectedBytes[i], ppuMemory.getOam(i), "Failed at position $i")
        }
    }

    @Test
    fun `OAM DMA writes get written to OAM memory with offset`() {
        val originallyWrittenBytes = Random.ubyteArrayOfSize(0x100)
        val subsequentlyWrittenBytes = Random.ubyteArrayOfSize(0x100)
        val ppuMemory = NesPpuMemory(getMapper())

        ppuMemory.oamDmaWrite(originallyWrittenBytes, 0)
        ppuMemory.oamDmaWrite(subsequentlyWrittenBytes, 0x80)

        for (i in 0 until 0x80) {
            assertEquals(
                originallyWrittenBytes[i],
                ppuMemory.getOam(i),
                "Failed at position $i of originallyWrittenCheck"
            )
        }
        for (i in 0 until 0x80) {
            assertEquals(
                subsequentlyWrittenBytes[i],
                ppuMemory.getOam(i + 0x80),
                "Failed at position $i of subsequentlyWrittenCheck"
            )
        }
    }

    private fun getMapper(): RomMapper {
        val rom = setupMemoryWithNES(
            0x1au,
            0x01u, // prgSize 4000
            0x02u, // chrSize 4000
            size = 0x8000 + HEADER_SIZE
        )

        return RomLoader.loadMapper(rom)
    }
}