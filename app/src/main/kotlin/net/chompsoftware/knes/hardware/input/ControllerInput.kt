package net.chompsoftware.knes.hardware.input

import net.chompsoftware.knes.toHex


interface ControllerInput {
    fun write(position: Int, value: UByte)
    fun read(position: Int): UByte

    fun getControllerO(): Controller
    fun getController1(): Controller

}

class NesControllerInput() : ControllerInput {
    private val controller0 = Controller()
    private val controller1 = Controller()

    override fun write(position: Int, value: UByte) {
        when (position) {
            CONTROLLER0_POSITION -> {
                controller0.write(value)
                controller1.write(value)
            }
        }
    }

    override fun read(position: Int): UByte {
        return when (position) {
            CONTROLLER0_POSITION -> controller0.read()
            CONTROLLER1_POSITION -> controller1.read()
            else -> throw Exception("Unexpected read from controller at ${position.toHex()}")
        }
    }

    override fun getControllerO(): Controller {
        return controller0
    }

    override fun getController1(): Controller {
        return controller1
    }
}


data class Controller(
    private var buttonA: Boolean = false,
    private var buttonB: Boolean = false,
    private var buttonSelect: Boolean = false,
    private var buttonStart: Boolean = false,
    private var buttonUp: Boolean = false,
    private var buttonDown: Boolean = false,
    private var buttonLeft: Boolean = false,
    private var buttonRight: Boolean = false,
    private var strobeBit: Boolean = false,
    private var strobePosition: Int = 0
) {
    fun write(value: UByte) {
        when (value) {
            CONTROLLER_POSITIVE -> {
                strobeBit = true
                strobePosition = 0
            }
            CONTROLLER_NEGATIVE -> strobeBit = false
            else -> TODO("Didn't understand the value $value being written to the controller")
        }
    }

    fun read(): UByte {
        if (strobeBit) {
            return buttonA.asController()
        }
        return when (strobePosition++) {
            0 -> buttonA
            1 -> buttonB
            2 -> buttonSelect
            3 -> buttonStart
            4 -> buttonUp
            5 -> buttonDown
            6 -> buttonLeft
            7 -> buttonRight
            else -> true
        }.asController()
    }

    fun getStrobeBit() = strobeBit

    private fun Boolean.asController() = if (this) CONTROLLER_POSITIVE else CONTROLLER_NEGATIVE

    fun getButtonA() = buttonA
    fun setButtonA(value: Boolean) {
        buttonA = value
    }

    fun getButtonB() = buttonB
    fun setButtonB(value: Boolean) {
        buttonB = value
    }

    fun getButtonSelect() = buttonSelect
    fun setButtonSelect(value: Boolean) {
        buttonSelect = value
    }

    fun getButtonStart() = buttonStart
    fun setButtonStart(value: Boolean) {
        buttonStart = value
    }
    fun getButtonUp() = buttonUp
    fun setButtonUp(value: Boolean) {
        buttonUp = value
        if (value) {
            buttonLeft = false
            buttonRight = false
            buttonDown = false
        }
    }

    fun getButtonDown() = buttonDown
    fun setButtonDown(value: Boolean) {
        buttonDown = value
        if (value) {
            buttonUp = false
            buttonLeft = false
            buttonRight = false
        }
    }

    fun getButtonLeft() = buttonLeft
    fun setButtonLeft(value: Boolean) {
        buttonLeft = value
        if (value) {
            buttonUp = false
            buttonDown = false
            buttonRight = false
        }
    }

    fun getButtonRight() = buttonRight
    fun setButtonRight(value: Boolean) {
        buttonRight = value
        if (value) {
            buttonUp = false
            buttonDown = false
            buttonLeft = false
        }
    }
}