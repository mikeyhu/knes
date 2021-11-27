package net.chompsoftware.knes.hardware.rom

import net.chompsoftware.knes.setupMemory
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExperimentalUnsignedTypes
class RomInspectorTest {
    @Test
    fun `Should validate NES1 rom`() {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(romInformation.romType, RomType.NES_1)
    }

    @Test
    fun `Should validate NES2 rom`() {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x0u,
            0x0u,
            0x0u,
            0xf8u,
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(romInformation.romType, RomType.NES_2)
    }

    @Test
    fun `Should throw an Error if not a NES rom`() {
        val rom = setupMemory(
            'S'.code.toUByte(),
            'E'.code.toUByte(),
            'G'.code.toUByte(),
            'A'.code.toUByte(),
            size = 16
        )
        assertThrows<RomLoadError> { RomInspector.inspectRom(rom) }
    }

    @Test
    fun `Should return NES1 nametable horizontal mirroring`() {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertFalse(romInformation.verticalMirroring)
    }

    @Test
    fun `Should return NES1 nametable vertical mirroring`() {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x0u,
            0x0u,
            0x1u,
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertTrue(romInformation.verticalMirroring)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00, 0x00, 0x00",
        "0x10, 0x00, 0x01",
        "0xff, 0x00, 0x0f",
        "0x00, 0xff, 0xf0",
        "0xff, 0xff, 0xff",
    )
    fun `Should retrieve NES1 mapper number`(byte6: Int, byte7: Int, mapper: Int) {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x0u,
            0x0u,
            byte6.toUByte(),
            byte7.toUByte(),
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(mapper, romInformation.mapper)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00, 0x0000",
        "0x01, 0x4000",
        "0x02, 0x8000"
    )
    fun `Should retrieve NES1 PRG ROM Size`(byte4: Int, size: Int) {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            byte4.toUByte(),
            0x0u,
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(size, romInformation.prgRom)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00, 0x0000",
        "0x01, 0x2000",
        "0x02, 0x4000"
    )
    fun `Should retrieve NES1 CHR ROM Size`(byte5: Int, size: Int) {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x0u,
            byte5.toUByte(),
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(size, romInformation.chrRom)
    }

    @ParameterizedTest
    @CsvSource(
        "0x000f",
        "0x2000",
        "0xffff"
    )
    fun `Should retrieve romSize`(size: Int) {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            size = size
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(size, romInformation.romSize)
    }

    @ParameterizedTest
    @CsvSource(
        "0x00,false",
        "0x02,true"
    )
    fun `Should retrieve batteryBackedRam`(bit6: Int, hasBatteryBackedRam: Boolean) {
        val rom = setupMemory(
            'N'.code.toUByte(),
            'E'.code.toUByte(),
            'S'.code.toUByte(),
            0x1au,
            0x0u,
            0x0u,
            bit6.toUByte(),
            size = 16
        )
        val romInformation = RomInspector.inspectRom(rom)

        assertEquals(hasBatteryBackedRam, romInformation.hasBatteryBackedRam)
    }
}

