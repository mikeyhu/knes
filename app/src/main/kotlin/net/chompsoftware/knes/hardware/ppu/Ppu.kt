package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.toInt16
import net.chompsoftware.knes.toLogHex
import java.awt.Color
import java.awt.image.BufferedImage

private const val screenHeight = 240
private const val screenWidth = 256 // only first 256 are visible
private const val tileSize = 8
private const val tileWidth = 32 // screenWidth / 8

class Ppu(private val ppuMemory: PpuMemory) {
    private var ppuAddressLow: UByte = 0x00u
    private var ppuAddressHigh: UByte = 0x00u

    private val memoryReadBuffer = MemoryReadBuffer()

    private val scanlineCounter = ScanlineCounter(0, 0)

    private var nextPpuWrite: Int = 0

    fun cpuTick(): Boolean {
        return scanlineCounter.cpuCycle()
    }

    private var screen = ByteArray(screenWidth * screenHeight)

    fun renderScreenAsBufferedImage(palette: Array<Color>):BufferedImage {
        for (h in 0 until screenHeight) {
            // each scanline
            val tileh = h / 8
            val hInTile = h % 8
            for (tilew in 0 until tileWidth) {
                //each row by tile
                val tileRequired = ppuMemory.get(0x2000 + tilew + (tileh * tileWidth)).toInt()
                val tileByteA = ppuMemory.get(tileRequired * 16 + hInTile)
                val tileByteB = ppuMemory.get(tileRequired * 16 + hInTile + 8)
                for (w in 0..7) {
                    //each tile by horizontal pixel
                    val pixelToSet = (h * screenWidth) + (tilew * tileSize) + w
                    val pixelValue = (tileByteA.bitAsByte(7 - w) + tileByteB.bitAsByte(7 - w) * 2).toByte()
                    screen[pixelToSet] = pixelValue
                }
            }
        }

        val img = BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB)
        for (h in 0 until screenHeight) {
            for (w in 0 until screenWidth) {
                val screenPixel = screen[(h * screenWidth) + w]
                img.setRGB(w, h, palette[screenPixel.toInt()].rgb)
            }
        }
        return img
    }

    private fun UByte.bitAsByte(position: Int): Byte {
        return this.toUInt().shr(position).and(1u).toByte()
    }

    fun busMemoryWriteEvent(position: Int, value: UByte) {
        println("PPU WRITE: $position => ${value.toLogHex()}")
        when (position) {
            PPU_REG_CONTROLLER -> {
                val ppuOperationState = PpuOperationState.fromUByte(value)
                println(ppuOperationState)
            }
            PPU_REG_ADDRESS -> {
                ppuAddressHigh = ppuAddressLow
                ppuAddressLow = value
                nextPpuWrite = toInt16(ppuAddressLow, ppuAddressHigh)
            }
            PPU_REG_DATA -> {
                ppuMemory.set(nextPpuWrite++, value)
            }
//            else -> TODO("busMemoryWriteEvent not implemented for ${position.toHex()}")
        }
    }

    fun busMemoryReadEvent(position: Int): UByte {
        if (position != 2) println("PPU READ: $position")
        return when (position) {
            PPU_REG_DATA -> {
                when (val ppuMemoryPosition = toInt16(ppuAddressLow, ppuAddressHigh)) {
                    in 0 until 0x2000 -> memoryReadBuffer.buffer {
                        ppuMemory.get(ppuMemoryPosition)
                    }
                    else -> TODO("read outside CHR-ROM not supported yet")
                }
            }
            else -> 0u // TODO("busMemoryReadEvent not implemented for ${position.toHex()}")
        }
    }

    private inner class MemoryReadBuffer(initialValue: UByte = 0u) {
        private var bufferedValue = initialValue

        fun buffer(nextItem: () -> UByte): UByte {
            return bufferedValue.also { bufferedValue = nextItem() }
        }
    }
}

class ScanlineCounter(
    var currentScanline: Int = 0,
    var currentScanlinePosition: Int = 0
) {
    /*
    NTSC systems have PPU 3 ticks per CPU
    PAL systems have 3.2 tickes per CPU
    This only supports NTSC for now
    https://www.nesdev.org/wiki/PPU
    */
    private val ppuTicksPerCpuTick = 3

    fun cpuCycle(): Boolean {

        currentScanlinePosition += ppuTicksPerCpuTick
        if (currentScanlinePosition > PPU_SCANLINE_SIZE) {
            currentScanlinePosition -= PPU_SCANLINE_SIZE
            currentScanline++
            if (currentScanline == PPU_SCANLINE_NMI_INTERRUPT) {
                return true
            }
            if (currentScanline >= PPU_SCANLINE_FRAME) {
                currentScanline = 0
            }
        }
        return false
    }
}
