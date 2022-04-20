package net.chompsoftware.knes.hardware.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random


class ControllerInputTest {

    @Test
    fun `Writing 1 to 0x4016 will reset both controllers positions`() {
        val controllerInput = NesControllerInput()

        assertFalse(controllerInput.getControllerO().getStrobeBit())
        assertFalse(controllerInput.getController1().getStrobeBit())

        controllerInput.write(CONTROLLER0_POSITION, CONTROLLER_POSITIVE)

        assertTrue(controllerInput.getControllerO().getStrobeBit())
        assertTrue(controllerInput.getController1().getStrobeBit())
    }

    @Test
    fun `Reading from 0x4016 will read controller0 data`() {
        val controllerInput = NesControllerInput()
        controllerInput.getControllerO().buttonA = true

        assertEquals(CONTROLLER_POSITIVE, controllerInput.read(CONTROLLER0_POSITION))
    }

    @Test
    fun `Reading from 0x4017 will read controller1 data`() {
        val controllerInput = NesControllerInput()
        controllerInput.getControllerO().buttonA = true

        assertEquals(CONTROLLER_POSITIVE, controllerInput.read(CONTROLLER0_POSITION))
    }

    @Nested
    inner class ControllerTest {

        @Test
        fun `continually reads Button A if Strobe is on`() {

            val controller = Controller(
                buttonA = true,
                strobeBit = true
            )

            for (i in 0..50) {
                assertEquals(CONTROLLER_POSITIVE, controller.read())
            }

            controller.buttonA = false

            for (i in 0..50) {
                assertEquals(CONTROLLER_NEGATIVE, controller.read())
            }
        }

        @Test
        fun `reads through all buttons if Strobe is off`() {
            for (i in 0..50) {
                val controller = Controller(
                    strobeBit = false
                )
                controller.buttonA = Random.nextBoolean()
                controller.buttonB = Random.nextBoolean()
                controller.buttonSelect = Random.nextBoolean()
                controller.buttonStart = Random.nextBoolean()
                controller.buttonUp = Random.nextBoolean()
                controller.buttonDown = Random.nextBoolean()
                controller.buttonLeft = Random.nextBoolean()
                controller.buttonRight = Random.nextBoolean()

                assertEquals(controller.buttonA.asController(), controller.read())
                assertEquals(controller.buttonB.asController(), controller.read())
                assertEquals(controller.buttonSelect.asController(), controller.read())
                assertEquals(controller.buttonStart.asController(), controller.read())
                assertEquals(controller.buttonUp.asController(), controller.read())
                assertEquals(controller.buttonDown.asController(), controller.read())
                assertEquals(controller.buttonLeft.asController(), controller.read())
                assertEquals(controller.buttonRight.asController(), controller.read())
            }
        }

        @Test
        fun `continually returns 1 after reading the 8 buttons if Strobe is off`() {
            val controller = Controller(
                strobeBit = false
            )
            for (i in 0..50) {
                if (i > 7) {
                    assertEquals(CONTROLLER_POSITIVE, controller.read())
                } else {
                    controller.read()
                }
            }
        }

        @Test
        fun `writing 1 will reset the position to 0 and turn the strobe off`() {
            val controller = Controller(
                buttonA = true,
                strobeBit = false
            )

            assertEquals(CONTROLLER_POSITIVE, controller.read())
            assertEquals(CONTROLLER_NEGATIVE, controller.read())
            controller.write(CONTROLLER_POSITIVE)
            assertEquals(CONTROLLER_POSITIVE, controller.read())
            assertEquals(CONTROLLER_POSITIVE, controller.read())
        }

        @Test
        fun `writing 0 will turn the strobe on`() {
            val controller = Controller(
                buttonA = true,
                strobeBit = false
            )

            assertEquals(CONTROLLER_POSITIVE, controller.read())
            assertEquals(CONTROLLER_NEGATIVE, controller.read())
            controller.write(CONTROLLER_POSITIVE)
            controller.write(CONTROLLER_NEGATIVE)
            assertEquals(CONTROLLER_POSITIVE, controller.read())
            assertEquals(CONTROLLER_NEGATIVE, controller.read())
        }

        private fun Boolean.asController() = if (this) CONTROLLER_POSITIVE else CONTROLLER_NEGATIVE
    }

}