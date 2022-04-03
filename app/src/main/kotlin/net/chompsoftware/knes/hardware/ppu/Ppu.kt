package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.toInt16
import net.chompsoftware.knes.toLogHex
import java.awt.Color
import java.awt.image.BufferedImage


class Ppu(private val ppuMemory: PpuMemory) {
    private var ppuAddressLow: UByte = 0x00u
    private var ppuAddressHigh: UByte = 0x00u
    private var nextPpuWrite: Int = 0

    private val memoryReadBuffer = MemoryReadBuffer()

    private val scanlineCounter = ScanlineCounter(0, 0, ::renderScanline)

    val bufferedImage = BufferedImage(HORIZONTAL_RESOLUTION, VERTICAL_RESOLUTION, BufferedImage.TYPE_INT_RGB)

    fun cpuTick(): Boolean {
        return scanlineCounter.cpuCycle()
    }

    private fun selectPalette(tileH: Int, tileW: Int): Array<Color> {
        val paletteByte = ppuMemory.get(0x2000 + 0x3c0 + (tileH / 4 * 8) + tileH / 4)

        val tc = tileH % 4 / 2
        val tw = tileW % 4 / 2
        val index = if (tc == 0) {
            if (tw == 0) {
                paletteByte.toInt().shr(2).and(0x11)
            } else {
                paletteByte.toInt().shr(6).and(0x11)
            }
        } else {
            if (tw == 0) {
                paletteByte.toInt().and(0x11)
            } else {
                paletteByte.toInt().shr(4).and(0x11)
            }
        }
        val start = index * 4 + 1
        return arrayOf(
            defaultPalette[ppuMemory.paletteTable(0)],
            defaultPalette[ppuMemory.paletteTable(start)],
            defaultPalette[ppuMemory.paletteTable(start + 1)],
            defaultPalette[ppuMemory.paletteTable(start + 2)]
        )
    }

    private fun renderScanline(scanlineRow: Int) {
        val tileRow = scanlineRow / 8
        val rowWithinTile = scanlineRow % 8
        for (tilew in 0 until TILES_PER_ROW) {
            //each row by tile
            val palette = selectPalette(tileRow, tilew)
            val tileRequired = ppuMemory.get(0x2000 + tilew + (tileRow * TILES_PER_ROW)).toInt()
            val tileByteA = ppuMemory.get(tileRequired * 16 + rowWithinTile)
            val tileByteB = ppuMemory.get(tileRequired * 16 + rowWithinTile + 8)
            for (w in 0..7) {
                //each tile by horizontal pixel
                bufferedImage.setRGB(
                    (tilew * TILE_SIZE) + w,
                    scanlineRow,
                    palette[pixelFor(tileByteA, tileByteB, w)].rgb
                )
            }
        }
    }

    private fun pixelFor(tile: UByte, tilePlus8: UByte, bit: Int) =
        tile.bitAsByte(7 - bit) + tilePlus8.bitAsByte(7 - bit) * 2


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
    var currentScanlinePosition: Int = 0,
    val onScanlineFinished: (Int) -> Unit
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
            if (currentScanline < (PPU_SCANLINE_VISIBLE)) {
                onScanlineFinished(currentScanline)
            }
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
