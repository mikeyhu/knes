package net.chompsoftware.knes.hardware.effects

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.Memory
import net.chompsoftware.knes.hardware.OperationState

abstract class Effect {
    abstract fun run(cpuState: CpuState, memory: Memory, operationState: OperationState)
    open fun requiresCycle(): Boolean = true
}

object ReadIntoAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setARegWithFlags(operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object ReadIntoX : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setXRegWithFlags(operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object ReadIntoY : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.setYRegWithFlags(operationState.getMemoryValue())
    }

    override fun requiresCycle() = false
}

object StoreAccumulator : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.aReg
    }
}

object StoreX : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.xReg
    }
}

object StoreY : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = cpuState.yReg
    }
}

object StoreMemory : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        memory[operationState.getLocation()] = operationState.getMemoryValue()
    }
}

object NoOperation : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
    }
}

object DoubleNoOperation : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
    }

    override fun requiresCycle() = false
}

object IncrementProgramCounter : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        cpuState.programCounterWithIncrement()
    }
}

object LocationFromBreak : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = cpuState.breakLocation
    }

    override fun requiresCycle() = false
}

class LocationFromInterrupt(private val location:Int) : Effect() {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState) {
        operationState.location = location
    }

    override fun requiresCycle() = false
}






