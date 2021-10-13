package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.*
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
fun processInstruction(cpuState: CpuState, memory: BasicMemory, operationState: OperationState) {
    var nextPipeline: EffectPipeline? = Operation.run(cpuState, memory, operationState)
    while (nextPipeline != null) {
        nextPipeline = nextPipeline.run(cpuState, memory, operationState)
    }
}

@ExperimentalUnsignedTypes
fun main() {
    println("Starting")
    val time = measureTimeMillis {
        val memory = BasicMemory(setupMemory(LDA_AB, 0x01u, 0x00u))
        val cpu = CpuState(0, 0x0u, 0x0u)
        val effectState = OperationState(0, null, null, null)
        for (i in 1..40_000_000) {

            processInstruction(cpu, memory, effectState)
            if (cpu.programCounter != 3) {
                throw Error("CPU State Incorrect")
            }
            cpu.programCounter = 0
            cpu.aReg = 0u
        }
    }
    println("Took: $time")

}
