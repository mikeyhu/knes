package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.ppu.Ppu
import net.chompsoftware.knes.toInt16


class CycleCoordinator(
    private val operation: EffectPipeline,
    private val ppu: Ppu,
    private val memory: Memory,
    bus: Bus,
    private val cpuState: CpuState = initialCpuState(memory),
    private val operationState: OperationState = OperationState(0)
) {
    init {
        bus.registerCallbackForCpuSuspend(::suspendCpu)
    }

    private var nextPipeline: EffectPipeline? = null
    private var cpuCyclesToSuspend = 0

    fun cycle(onNMICallback: () -> Unit) {
        val isNMIInterrupt = ppu.cpuTick(onNMICallback)
        if (isNMIInterrupt) {
            cpuState.isNMIInterrupt = true
        }
        if (cpuCyclesToSuspend <= 0) {
            nextPipeline = (nextPipeline ?: operation).run(cpuState, memory, operationState)
        } else {
            cpuCyclesToSuspend--
        }
    }

    private fun suspendCpu(cycles: Int) {
        println("Suspending for $cycles cycles")
        cpuCyclesToSuspend = cycles
    }

    companion object {
        private fun initialCpuState(memory: Memory) = CpuState(
            programCounter = toInt16(memory[0xfffc], memory[0xfffd]),
            breakLocation = BREAK_LOCATION
        )
    }
}