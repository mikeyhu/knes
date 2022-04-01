package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.toInt16
import net.chompsoftware.knes.toLogHex

class Ppu(private val ppuMemory: PpuMemory) {
    private var ppuAddressLow: UByte = 0x00u
    private var ppuAddressHigh: UByte = 0x00u

    private val memoryReadBuffer = MemoryReadBuffer()

    private val scanlineCounter = ScanlineCounter(0, 0)

    private var nextPpuWrite: Int = 0

    fun cpuTick(): Boolean {
        return scanlineCounter.cpuCycle()
    }

    fun getPpuMemory(position: Int) = ppuMemory.get(position)

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
