package net.chompsoftware.knes


import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.hardware.input.ControllerInput
import net.chompsoftware.knes.hardware.input.NesControllerInput
import net.chompsoftware.knes.hardware.ppu.HORIZONTAL_RESOLUTION
import net.chompsoftware.knes.hardware.ppu.NesPpuMemory
import net.chompsoftware.knes.hardware.ppu.NesPpu
import net.chompsoftware.knes.hardware.ppu.VERTICAL_RESOLUTION
import net.chompsoftware.knes.hardware.rom.RomLoader
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

fun processInstruction(cpuState: CpuState, memory: BasicMemory, operationState: OperationState) {
    var nextPipeline: EffectPipeline? = Operation.run(cpuState, memory, operationState)
    while (nextPipeline != null) {
        nextPipeline = nextPipeline.run(cpuState, memory, operationState)
    }
}

const val verticalMultiple = 4
const val horizontalMultiple = 4

class App(ppu: NesPpu, controllerInput: ControllerInput) : JFrame() {

    init {
        title = "KNES"
        setSize(HORIZONTAL_RESOLUTION * horizontalMultiple, VERTICAL_RESOLUTION * verticalMultiple)
        add(RenderSurface(ppu))

        addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent?) {}

            override fun windowClosing(e: WindowEvent?) {
                exitProcess(0)
            }

            override fun windowClosed(e: WindowEvent?) {}

            override fun windowIconified(e: WindowEvent?) {}

            override fun windowDeiconified(e: WindowEvent?) {}

            override fun windowActivated(e: WindowEvent?) {}

            override fun windowDeactivated(e: WindowEvent?) {}

        })

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {
                e?.also {
                    setKey(it, true)
                }
            }

            override fun keyReleased(e: KeyEvent?) {
                e?.also {
                    setKey(it, false)
                }
            }

            private fun setKey(keyEvent: KeyEvent, value: Boolean) {
                when (keyEvent.keyCode) {
                    KeyEvent.VK_A -> controllerInput.getControllerO().buttonLeft = value
                    KeyEvent.VK_S -> controllerInput.getControllerO().buttonDown = value
                    KeyEvent.VK_D -> controllerInput.getControllerO().buttonRight = value
                    KeyEvent.VK_W -> controllerInput.getControllerO().buttonUp = value
                    KeyEvent.VK_F -> controllerInput.getControllerO().buttonA = value
                    KeyEvent.VK_G -> controllerInput.getControllerO().buttonB = value
                    KeyEvent.VK_R -> controllerInput.getControllerO().buttonSelect = value
                    KeyEvent.VK_T -> controllerInput.getControllerO().buttonStart = value
                }
            }
        })
    }
}

class RenderSurface(val ppu: NesPpu) : JPanel() {
    var repaints = 0

    var currentFrame = 0
    var maxFrame = 0

    var second = System.currentTimeMillis() / 1000

    override fun paintComponent(g: Graphics) {
        val currentSecond = System.currentTimeMillis() / 1000
        if (currentSecond != second) {
            maxFrame = currentFrame
            currentFrame = 0
            second = currentSecond

        } else {
            currentFrame++
        }
        super.paintComponent(g)
        val g2d = g as Graphics2D

        val image = ppu.getFinishedImage()
        g2d.drawImage(
            image,
            0,
            0,
            HORIZONTAL_RESOLUTION * horizontalMultiple,
            VERTICAL_RESOLUTION * verticalMultiple,
            this
        )

        g2d.color = Color.lightGray
        g2d.drawString("repaints: ${repaints++}, per second: ${maxFrame}", 10, 10)

    }
}


fun main() {

//    val file = File("../nes-test-roms/instr_test-v3/rom_singles/" + "13-rti.nes")
//    val file = File("../nes-test-roms/full_palette/full_palette.nes")
//    val file = File("../nes-test-rom¢®s/PaddleTest3/PaddleTest.nes")
    val file = File("../../emulation/nes/pacman.nes")
//    val file = File("otherRoms/color_test.nes")

    val mapper = RomLoader.loadMapper(readFileToByteArray(file))
    val ppu = NesPpu(NesPpuMemory(mapper))
    val controllerInput = NesControllerInput()
    val bus = NesBus(ppu, controllerInput)
    val memory = NesMemory(
        mapper, bus, failOnReadError = false, failOnWriteError = false
    )

    val cycleCoordinator = CycleCoordinator(Operation, ppu, memory, bus)

    println("starting app")

    Configuration.limitSpeed = true

    val app = App(ppu, controllerInput)
    app.isVisible = true

    println("started app")

    var ticksDone = 0
    try {
        while (true) {
            cycleCoordinator.cycle(onNMICallback = app::repaint)
            ticksDone++
        }
    } catch (e: Throwable) {
        println(e)
    }
    println("finished at $ticksDone")

}
