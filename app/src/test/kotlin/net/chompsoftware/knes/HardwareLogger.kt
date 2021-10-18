package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.*


@ExperimentalUnsignedTypes
class HardwareLogger(private val cpuState: CpuState, private val memory: Memory) {

    val ENABLE_LOGGING = true
    val LOGGING_RANGES = listOf(
        0x5d0 to 0x6d0
    )

    private var cycleLog: MutableList<MutableList<Activity>> = mutableListOf()

    private val trackingMemory: Memory by lazy {
        TrackingMemory(memory)
    }

    fun processInstruction(operationState: OperationState = OperationState(0, null, null, null)) {

        val counter = cpuState.programCounter
        nextCycle()
        var nextPipeline: EffectPipeline? = Operation.run(cpuState, trackingMemory, operationState)

        while (nextPipeline != null) {
            nextCycle()
            nextPipeline = nextPipeline.run(cpuState, trackingMemory, operationState)
        }
        if (ENABLE_LOGGING && inLoggingRange(cpuState.programCounter)) {
            println("programCounter:${counter.toHex()}")
            println(cpuState)
            println(cycleLog)
        }
        cycleLog = mutableListOf()
    }

    fun inLoggingRange(counter: Int) = LOGGING_RANGES.any { (low, high) ->
        counter in low until high
    }

    private fun log(activity: Activity) {
        cycleLog.last().add(activity)
    }

    private fun nextCycle() {
        cycleLog.add(mutableListOf())
    }

    inner class TrackingMemory(val memory: Memory) : Memory {

        override fun get(position: Int): UByte {
            return memory[position].also {
                log(Activity.MemoryReadActivity(position, it))
            }
        }

        override fun set(position: Int, value: UByte) {
            memory.set(position, value)
            log(Activity.MemoryWriteActivity(position, value))
        }
    }
}









