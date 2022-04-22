package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.toInt16
import net.chompsoftware.knes.toLogHex
import java.awt.Color
import java.awt.image.BufferedImage


interface Ppu {
    fun cpuTick(onNMIInterrupt: () -> Unit): Boolean
    fun getFinishedImage(): BufferedImage
    fun busMemoryWriteEvent(position: Int, value: UByte)
    fun busMemoryReadEvent(position: Int): UByte
    fun oamDmaWrite(bytes: UByteArray)
}

class NesPpu(
    private val ppuMemory: PpuMemory,
    scanlineCounterOverride: ScanlineCounter? = null
) : Ppu {
    /* Default state :https://www.nesdev.org/wiki/PPU_power_up_state
    Register                            Power       Reset
    PPUCTRL ($2000)                     0000 0000   0000 0000
    PPUMASK ($2001)                     0000 0000   0000 0000
    PPUSTATUS ($2002)                   +0+x xxxx   U??x xxxx
    OAMADDR ($2003)                     $00         unchanged1
    $2005 / $2006 latch                 cleared     cleared
    PPUSCROLL ($2005)                   $0000       $0000
    PPUADDR ($2006)                     $0000       unchanged
    PPUDATA ($2007) read buffer         $00         $00
    odd frame                           no          no
    OAM                                 unspecified unspecified
    Palette                             unspecified unchanged
    NT RAM (external, in Control Deck)  unspecified unchanged
    CHR RAM (external, in Game Pak)     unspecified unchanged
     */
    private var ppuAddressLow: UByte = 0x00u
    private var ppuAddressHigh: UByte = 0x00u
    private var nextPpuWrite: Int = 0
    private var nextOamWrite: Int = 0
    private val memoryReadBuffer = MemoryReadBuffer()
    private var ppuOperationState = PpuOperationState.fromUByte(1u)

    val scanlineCounter = scanlineCounterOverride ?: ScanlineCounter(0, 0, ::renderScanline)

    private var finishedImageSwitch = false
    private val bufferedImage0 = BufferedImage(HORIZONTAL_RESOLUTION, VERTICAL_RESOLUTION, BufferedImage.TYPE_INT_RGB)
    private val bufferedImage1 = BufferedImage(HORIZONTAL_RESOLUTION, VERTICAL_RESOLUTION, BufferedImage.TYPE_INT_RGB)

    private val nesPpuStatus = NesPpuStatus(false)

    private val nesScrollStatus = NesScrollStatus()

    override fun cpuTick(onNMIInterrupt: () -> Unit): Boolean {
        val isNMIInterrupt = scanlineCounter.cpuCycle()

        if (isNMIInterrupt) {
            finishedImageSwitch = !finishedImageSwitch
            onNMIInterrupt()
        }
        return isNMIInterrupt && ppuOperationState.generateNMIOnInterval
    }

    override fun getFinishedImage() = if (finishedImageSwitch) bufferedImage0 else bufferedImage1

    private fun getInProgressImage() = if (finishedImageSwitch) bufferedImage1 else bufferedImage0

    private fun selectPalette(tileH: Int, tileW: Int): Array<Color> {
        val paletteNumber = selectPaletteNumber(ppuMemory, tileW, tileH)
        val start = paletteNumber * 4 + 1

        return arrayOf(
            defaultPalette[ppuMemory.paletteTable(0)],
            defaultPalette[ppuMemory.paletteTable(start)],
            defaultPalette[ppuMemory.paletteTable(start + 1)],
            defaultPalette[ppuMemory.paletteTable(start + 2)]
        )
    }

    private fun selectPalette(paletteNumber: Int): Array<Color> {
        val start = paletteNumber * 4 + 1

        return arrayOf(
            defaultPalette[ppuMemory.paletteTable(0)],
            defaultPalette[ppuMemory.paletteTable(start)],
            defaultPalette[ppuMemory.paletteTable(start + 1)],
            defaultPalette[ppuMemory.paletteTable(start + 2)]
        )
    }


    private fun renderScanline(scanlineRow: Int) {
        renderBackgroundScanline(scanlineRow)
        renderSpriteScanline(scanlineRow)
    }

    private fun renderBackgroundScanline(scanlineRow: Int) {
        val bufferedImage = getInProgressImage()
        if (scanlineRow == PPU_SCANLINE_VISIBLE - 1) {
            // last row that calls renderScanline
            nesPpuStatus.setInVBlank(true)
        }
        val tileRow = scanlineRow / 8
        val rowWithinTile = scanlineRow % 8
        for (tilew in 0 until TILES_PER_ROW) {
            // row by tile
            val palette = selectPalette(tileRow, tilew)
            val tileRequired = ppuMemory.get(ppuOperationState.baseNametableAddress + tilew + (tileRow * TILES_PER_ROW)).toInt()
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

    private fun renderSpriteScanline(scanlineRow: Int) {
        val bufferedImage = getInProgressImage()
        for (spriteNum in MAX_SPRITES - 1 downTo 0) {
            val spriteYPosition = ppuMemory.oam().spriteYPosition(spriteNum) + 1
            if (spriteYPosition > scanlineRow - 8 && spriteYPosition <= scanlineRow) {
                //sprite on row
                val spriteIndex = ppuMemory.oam().spriteIndexNumber(spriteNum)
                val spriteAttributes = ppuMemory.oam().spriteAttributes(spriteNum)

                val spriteLine = (scanlineRow - spriteYPosition).let {
                    if (spriteAttributes.spriteFlipVertical()) flip(it) else it
                }
                val tileByteA = ppuMemory.get(ppuOperationState.spritePatternAddress + spriteIndex * 16 + spriteLine)
                val tileByteB = ppuMemory.get(ppuOperationState.spritePatternAddress + spriteIndex * 16 + spriteLine + 8)
                val palette = selectPalette(spriteAttributes.spritePalette() + 4)
                for (w in 0..7) {
                    val offset = if (spriteAttributes.spriteFlipHorizontal()) flip(w) else w
                    //each tile by horizontal pixel
                    val pixel = pixelFor(tileByteA, tileByteB, w)
                    if (pixel > 0) {
                        val pixelXPosition = ppuMemory.oam().spriteXPosition(spriteNum) + offset
                        if (pixelXPosition < HORIZONTAL_RESOLUTION) {
                            bufferedImage.setRGB(
                                pixelXPosition,
                                scanlineRow,
                                palette[pixel].rgb
                            )
                        }
                    }
                }
            }
        }
    }

    private fun pixelFor(tile: UByte, tilePlus8: UByte, bit: Int) =
        tile.bitAsByte(7 - bit) + tilePlus8.bitAsByte(7 - bit) * 2


    private fun UByte.bitAsByte(position: Int): Byte {
        return this.toUInt().shr(position).and(1u).toByte()
    }

    override fun busMemoryWriteEvent(position: Int, value: UByte) {
        when (position) {
            PPU_REG_CONTROLLER -> {
                ppuOperationState = PpuOperationState.fromUByte(value)
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
            PPU_REG_OAM_ADDRESS -> {
                nextOamWrite = value.toInt()
            }
            PPU_REG_SCROLL -> {
                nesScrollStatus.write(value)
            }
            else -> println("PPU IGNORED WRITE: $position => ${value.toLogHex()}")
        }
    }

    override fun busMemoryReadEvent(position: Int): UByte {
//        println("PPU READ: $position")
        return when (position) {
            PPU_REG_DATA -> {
                when (val ppuMemoryPosition = toInt16(ppuAddressLow, ppuAddressHigh)) {
                    in 0 until 0x4000 -> memoryReadBuffer.buffer {
                        ppuMemory.get(ppuMemoryPosition)
                    }
                    else -> TODO("read outside CHR-ROM not supported yet")
                }
            }
            PPU_REG_STATUS -> {
                nesPpuStatus.toStatusUByte()
            }
            else -> 0u // TODO("busMemoryReadEvent not implemented for ${position.toHex()}")
        }
    }

    override fun oamDmaWrite(bytes: UByteArray) {
        ppuMemory.oamDmaWrite(bytes, nextOamWrite)
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

class NesPpuStatus(
    private var inVBlank: Boolean = false
) {

    private fun getInVBlank(): Boolean {
        return inVBlank.also {
            inVBlank = false
        }
    }

    fun setInVBlank(value: Boolean) {
        inVBlank = value
    }

    fun toStatusUByte(): UByte {
        return if (getInVBlank()) 0x80u else 0u
    }
}
