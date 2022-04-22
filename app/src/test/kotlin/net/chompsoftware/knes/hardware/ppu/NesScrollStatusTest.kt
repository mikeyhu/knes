package net.chompsoftware.knes.hardware.ppu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class NesScrollStatusTest {
    @Test
    fun `Writing alternates between X and Y, starting at X`() {
        val scrollStatus = NesScrollStatus()

        scrollStatus.write(0x1u)
        scrollStatus.write(0x2u)

        assertEquals(0x1, scrollStatus.getX())
        assertEquals(0x2, scrollStatus.getY())
    }

    @Test
    fun `Calling reset will reset the back to X and will reset the scroll values`() {
        val scrollStatus = NesScrollStatus()

        scrollStatus.write(0x1u)
        scrollStatus.reset()
        scrollStatus.write(0x2u)

        assertEquals(0x2, scrollStatus.getX())
        assertEquals(0x0, scrollStatus.getY())
    }
}
