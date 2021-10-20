package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Effect
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.toHex


object Jump : Effect() {
    @ExperimentalUnsignedTypes
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
//        println("Jumping from ${cpuState.programCounter.toHex()} to ${operationState.getLocation().toHex()}")
        cpuState.programCounter = operationState.getLocation()
    }

    override fun requiresCycle() = false
}