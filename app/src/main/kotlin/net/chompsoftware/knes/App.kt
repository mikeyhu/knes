package net.chompsoftware.knes


import net.chompsoftware.knes.app.FPSTimer
import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.hardware.input.ControllerInput
import net.chompsoftware.knes.hardware.input.NesControllerInput
import net.chompsoftware.knes.hardware.ppu.HORIZONTAL_RESOLUTION
import net.chompsoftware.knes.hardware.ppu.NesPpuMemory
import net.chompsoftware.knes.hardware.ppu.NesPpu
import net.chompsoftware.knes.hardware.ppu.VERTICAL_RESOLUTION
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.readZipToUByteArray
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
                    KeyEvent.VK_A -> controllerInput.getControllerO().setButtonLeft(value)
                    KeyEvent.VK_S -> controllerInput.getControllerO().setButtonDown(value)
                    KeyEvent.VK_D -> controllerInput.getControllerO().setButtonRight(value)
                    KeyEvent.VK_W -> controllerInput.getControllerO().setButtonUp(value)
                    KeyEvent.VK_F -> controllerInput.getControllerO().setButtonA(value)
                    KeyEvent.VK_G -> controllerInput.getControllerO().setButtonB(value)
                    KeyEvent.VK_R -> controllerInput.getControllerO().setButtonSelect(value)
                    KeyEvent.VK_T -> controllerInput.getControllerO().setButtonStart(value)

                    KeyEvent.VK_J -> controllerInput.getController1().setButtonLeft(value)
                    KeyEvent.VK_K -> controllerInput.getController1().setButtonDown(value)
                    KeyEvent.VK_L -> controllerInput.getController1().setButtonRight(value)
                    KeyEvent.VK_I -> controllerInput.getController1().setButtonUp(value)
                    KeyEvent.VK_COLON -> controllerInput.getController1().setButtonA(value)
                    KeyEvent.VK_QUOTEDBL -> controllerInput.getController1().setButtonB(value)
                    KeyEvent.VK_P -> controllerInput.getController1().setButtonSelect(value)
                    KeyEvent.VK_BRACELEFT -> controllerInput.getController1().setButtonStart(value)
                }
            }
        })
    }
}

class RenderSurface(val ppu: NesPpu) : JPanel() {
    val timer = FPSTimer()

    override fun paintComponent(g: Graphics) {
        timer.increment()
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
        if (Configuration.showFPS) {
            g2d.color = Color.lightGray
            g2d.drawString("FPS per second: ${timer.mostRecent()}", 10, 10)
        }
    }
}

fun main() {

//    val file = File("../nes-test-roms/instr_test-v3/rom_singles/" + "13-rti.nes")
//    val file = File("../nes-test-roms/spritecans-2011/spritecans.nes")
//    val file = File("../nes-test-roms/full_palette/full_palette.nes")
//    val file = File("../nes-test-roms/PaddleTest3/PaddleTest.nes")
//    val file = File("otherRoms/color_test.nes")
//    val file = File("../../emulation/nes/pacman.nes")
//    val file = File("../../emulation/nes/Galaga.zip")
    val file = File("../../emulation/nes/Xevious.zip")

    val mapper = if (file.name.endsWith(".nes")) {
        RomLoader.loadMapper(readFileToByteArray(file))
    } else if (file.name.endsWith(".zip")) {
        RomLoader.loadMapper(readZipToUByteArray(file))
    } else TODO("unsupported file extension")

    val ppu = NesPpu(NesPpuMemory(mapper))
    val controllerInput = NesControllerInput()
    val bus = NesBus(ppu, controllerInput)
    val memory = NesMemory(mapper, bus, failOnReadError = false, failOnWriteError = false)

    val cycleCoordinator = CycleCoordinator(Operation, ppu, memory, bus)

    Configuration.limitSpeed = true
    Configuration.showFPS = false

    val app = App(ppu, controllerInput)
    app.isVisible = true

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

