package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.PPU_REG_OAM_DMA
import net.chompsoftware.knes.hardware.rom.HEADER_SIZE
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.RomMapper
import net.chompsoftware.knes.hardware.utilities.nextUByte
import net.chompsoftware.knes.hardware.utilities.nextUByteNotZero
import net.chompsoftware.knes.hardware.utilities.setupMemoryWithNES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NesMemoryTest {

    inner class FakeRomMapper : RomMapper {
        override fun getPrgRom(position: Int): UByte {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun getChrRom(position: Int): UByte {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun getChrRomSlice(position: Int, size: Int): UByteArray {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun getBatteryBackedRam(position: Int): UByte {
            throw Error("Interacting with FakeRomMapper")
        }

        override fun setBatteryBackedRam(position: Int, value: UByte) {
            throw Error("Interacting with FakeRomMapper")
        }
    }

    inner class FakeBus(
        var fakeReadValue: UByte = 0u,
        var fakeOamDmaReceived: UByteArray? = null
    ) : Bus {

        var mostRecentEvent: BusEvent? = null

        override fun ppuRegisterWrite(position: Int, value: UByte) {
            mostRecentEvent = BusWriteEvent(position, value)
        }

        override fun ppuRegisterRead(position: Int): UByte {
            mostRecentEvent = BusReadEvent(position, fakeReadValue)
            return fakeReadValue
        }

        override fun performCallbackForCpuSuspend(cycles: Int) {
            fail("should not be used")
        }

        override fun registerCallbackForCpuSuspend(callback: (Int) -> Unit) {
            fail("should not be used")
        }

        override fun oamDmaWrite(bytes: UByteArray) {
            fakeOamDmaReceived = bytes
        }
    }

    @Test
    fun `Has 2KB of RAM mirrored 3 times`() {
        val memory = NesMemory(FakeRomMapper(), FakeBus())

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
    fun `Has PPU Registered mirrored from 0x2000 until 0x3fff for read`() {
        val bus = FakeBus()
        val memory = NesMemory(FakeRomMapper(), bus)

        for (i in 0x2000..0x3fff) {
            assertEquals(0u, memory[i].toUInt(), "PPU should be zeroed out at $i")
        }

        for (i in 0 until 0x8) {
            val byte = Random.nextUByteNotZero()
            bus.fakeReadValue = byte
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
    fun `Raises bus events on PPU write`() {
        val bus = FakeBus()
        val memory = NesMemory(FakeRomMapper(), bus)

        for (i in 0x2000..0x3fff) {
            memory[i] = 0x11u
            assertEquals(BusWriteEvent((i % 8), 0x11u), bus.mostRecentEvent)
        }
    }

    @Test
    fun `Raises bus events on PPU read`() {
        val bus = FakeBus()
        val memory = NesMemory(FakeRomMapper(), bus)

        for (i in 0x2000..0x3fff) {
            bus.fakeReadValue = Random.nextUByte()
            memory[i]
            assertEquals(BusReadEvent((i % 8), bus.fakeReadValue), bus.mostRecentEvent)
        }
    }

    @Test
    fun `Maps memory above 0x8000 into the mapper`() {
        val rom = setupMemoryWithNES(
            0x1au,
            0x02u, // prgSize 8000
            size = 0x8000 + HEADER_SIZE
        )

        for (i in 0 until 0x8000) {
            rom[i + HEADER_SIZE] = Random.nextUByteNotZero()
        }
        val mapper = RomLoader.loadMapper(rom)
        val memory = NesMemory(mapper, FakeBus())

        for (i in 0x8000 until 0x10000) {
            assertEquals(mapper.getPrgRom(i), memory[i])
        }
    }

    @Test
    fun `Performs OAM DMA when initiated by a write to 0x4014`() {
        val bus = FakeBus()
        val memory = NesMemory(FakeRomMapper(), bus)

        for (i in 0x0..0xff) {
            memory[0x0700 + i] = i.toUByte()
        }

        memory[PPU_REG_OAM_DMA] = 0x07u

        assertNotNull(bus.fakeOamDmaReceived)
        bus.fakeOamDmaReceived?.also {
            for (i in 0x0..0xff) {
                assertEquals(i.toUByte(), it[i])
            }
        }
    }
}

interface BusEvent

data class BusWriteEvent(val position: Int, val value: UByte) : BusEvent
data class BusReadEvent(val position: Int, val value: UByte) : BusEvent