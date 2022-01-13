package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.ppu.TileReader
import net.chompsoftware.knes.hardware.ppu.defaultPalette
import net.chompsoftware.knes.hardware.rom.RomInspector
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.rom.RomMapper
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel

class RomViewerApp(romMapper: RomMapper) : JFrame() {
    init {
        title = "KNES - RomViewer"
        setSize(1024, 800)
        defaultCloseOperation = EXIT_ON_CLOSE
        add(TileViewSurface(romMapper))
    }
}

class TileViewSurface(private val romMapper: RomMapper) : JPanel() {

    private val tileSize = 8 * 4
    private val rowSize = 32

    private val palette = arrayOf(
        defaultPalette[21],
        defaultPalette[41],
        defaultPalette[39],
        defaultPalette[34],
    )

    val images: Array<BufferedImage> = Array(512) { t ->
        TileReader.getTileFromMemory(romMapper.getChrRomSlice(t * 16, 16)).asBufferedImage(palette)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        images.mapIndexed { index, bufferedImage ->
            g2d.drawImage(
                bufferedImage,
                index % rowSize * tileSize,
                index / rowSize * tileSize,
                tileSize,
                tileSize,
                this
            )
        }
    }
}

fun main(args: Array<String>) {
    val inspector = RomInspector
    val fileData = readFileToByteArray(File(args[0]))

    println(inspector.inspectRom(fileData))

    val app = RomViewerApp(RomLoader.loadMapper(fileData))
    app.isVisible = true
}