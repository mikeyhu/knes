package net.chompsoftware.knes


import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.hardware.ppu.NesPpuMemory
import net.chompsoftware.knes.hardware.ppu.Ppu
import net.chompsoftware.knes.hardware.ppu.TileReader
import net.chompsoftware.knes.hardware.ppu.defaultPalette
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.RomMapper
import java.awt.Graphics
import java.awt.Graphics2D
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

const val resolutionMultiplier = 4

class App(ppu: Ppu, mapper: RomMapper) : JFrame() {

    init {
        title = "KNES"
        setSize(320 * resolutionMultiplier, 240 * resolutionMultiplier)
        add(RenderSurface(ppu, mapper))

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
    }
}

class RenderSurface(val ppu: Ppu, val mapper: RomMapper) : JPanel() {
    var repaints = 0

    var currentFrame = 0
    var maxFrame = 0

    var second = System.currentTimeMillis() / 1000

    private val palette = arrayOf(
        defaultPalette[21],
        defaultPalette[41],
        defaultPalette[39],
        defaultPalette[34],
    )

    val tileSize = 8 * resolutionMultiplier

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

        for (t in 0x0000..0x03c0) {
            val tile = ppu.getPpuMemory(t + 0x2000)
            val x = t % 32
            val y = t / 32

            val slice = mapper.getChrRomSlice(tile.toInt() * 16, 16)
            val image = TileReader.getTileFromMemory(slice).asBufferedImage(palette)
            g2d.drawImage(
                image, x * tileSize, y * tileSize, tileSize, tileSize, this
            )
        }

        g2d.drawString("repaints: ${repaints++}, per second: ${maxFrame}", 10, 10)
    }
}


fun main() {

    val file = File("../nes-test-roms/instr_test-v3/rom_singles/" + "13-rti.nes")
//    val file = File("../../emulation/nes/pacman.nes")

    val mapper = RomLoader.loadMapper(readFileToByteArray(file))
    val ppu = Ppu(NesPpuMemory(mapper))
    val memory = NesMemory(
        mapper, NesBus(ppu), failOnReadError = false, failOnWriteError = false
    )

    val initialCounter = toInt16(memory[0xfffc], memory[0xfffd])

    println(initialCounter.toLogHex())

    val cpuState = CpuState(
        programCounter = initialCounter, breakLocation = 0xfffe
    )

    println("starting app")

    val app = App(ppu, mapper)
    app.isVisible = true

    println("started app")

    val operationState = OperationState(0)
    var ticksDone = 0
    try {
        var nextPipeline: EffectPipeline? = null
        while (true) {
            val nmiInterrupt = ppu.cpuTick()
            nextPipeline = if (nextPipeline != null) {
                nextPipeline.run(cpuState, memory, operationState)
            } else {
                Operation.run(cpuState, memory, operationState)
            }
            if (nmiInterrupt) {
                app.repaint()
            }
            ticksDone++
        }
    } catch (e: Throwable) {
        println(e.message)
    }
    println(ticksDone)

}
