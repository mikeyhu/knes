package net.chompsoftware.knes.hardware.ppu

import net.chompsoftware.knes.toInt16
import net.chompsoftware.knes.toLogHex

class Ppu(private val ppuMemory: PpuMemory) {
    private var ppuAddressLow: UByte = 0x00u
    private var ppuAddressHigh: UByte = 0x00u

    private val memoryReadBuffer = MemoryReadBuffer()

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
            }
//            else -> TODO("busMemoryWriteEvent not implemented for ${position.toHex()}")
        }
    }

    fun busMemoryReadEvent(position: Int): UByte {
        println("PPU READ: $position")
        return when (position) {
            PPU_REG_DATA -> {
                val ppuMemoryPosition = toInt16(ppuAddressLow, ppuAddressHigh)
                when (ppuMemoryPosition) {
                    in 0 until 0x2000 -> memoryReadBuffer.buffer {
                        ppuMemory.get(
                            toInt16(
                                ppuAddressLow,
                                ppuAddressHigh
                            )
                        )
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
