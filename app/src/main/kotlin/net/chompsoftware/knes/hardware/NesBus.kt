package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.OAM_CPU_SUSPEND_CYCLES
import net.chompsoftware.knes.hardware.ppu.Ppu

interface Bus {
    fun ppuRegisterWrite(position: Int, value: UByte)
    fun ppuRegisterRead(position: Int): UByte
    fun performCallbackForCpuSuspend(cycles: Int)
    fun registerCallbackForCpuSuspend(callback: (Int) -> Unit)
    fun oamDmaWrite(bytes: UByteArray)
}

class NesBus(private val ppu: Ppu) : Bus {
    private var cpuSuspendCallback: ((Int) -> Unit)? = null

    override fun ppuRegisterWrite(position: Int, value: UByte) {
        ppu.busMemoryWriteEvent(position, value)
    }

    override fun ppuRegisterRead(position: Int): UByte {
        return ppu.busMemoryReadEvent(position)
    }

    override fun performCallbackForCpuSuspend(cycles: Int) {
        cpuSuspendCallback?.invoke(cycles)
    }

    override fun registerCallbackForCpuSuspend(callback: (Int) -> Unit) {
        cpuSuspendCallback = callback
    }

    override fun oamDmaWrite(bytes: UByteArray) {
        ppu.oamDmaWrite(bytes)
        cpuSuspendCallback?.invoke(OAM_CPU_SUSPEND_CYCLES)
    }
}