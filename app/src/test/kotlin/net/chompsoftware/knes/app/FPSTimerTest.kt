package net.chompsoftware.knes.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class FPSTimerTest {

    @Test
    fun `returns the most recent finished FPS`() {
        var currentSecond = 1L
        fun fakeSecondFunction() = currentSecond

        val timer = FPSTimer(::fakeSecondFunction)

        assertEquals(0, timer.mostRecent())
        timer.increment()
        assertEquals(0, timer.mostRecent())
    }

    @Test
    fun `counts frames until the second changes`() {
        var currentSecond = 1L
        fun fakeSecondFunction() = currentSecond

        val timer = FPSTimer(::fakeSecondFunction)

        timer.increment()
        timer.increment()
        assertEquals(0, timer.mostRecent())
        currentSecond = 2L
        timer.increment()
        assertEquals(2, timer.mostRecent())
    }
}