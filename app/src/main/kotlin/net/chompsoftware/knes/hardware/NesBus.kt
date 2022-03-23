package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.Ppu

interface Bus {
    fun ppuRegisterWrite(position: Int, value: UByte)
    fun ppuRegisterRead(position: Int): UByte
}

class NesBus(private val ppu: Ppu) : Bus {
    override fun ppuRegisterWrite(position: Int, value: UByte) {
        ppu.busMemoryWriteEvent(position, value)
    }

    override fun ppuRegisterRead(position: Int): UByte {
        return ppu.busMemoryReadEvent(position)
    }
}