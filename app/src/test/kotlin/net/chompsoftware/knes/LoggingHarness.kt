package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.*

@ExperimentalUnsignedTypes
class LoggingHarness(private val cpuState: CpuState, private val memory: Memory, maxSize: Int = 10) {

    private val processLog = ProcessLog(maxSize)

    fun processInstruction(operationState: OperationState = OperationState(0, null, null, null)) {
        val processLogEntry = ProcessLogEntry(cpuState)

        val trackingMemory = TrackingMemory(memory, processLogEntry)

        processLog.add(processLogEntry)

        processLogEntry.nextCycle()
        var nextPipeline: EffectPipeline? = Operation.run(cpuState, trackingMemory, operationState)

        while (nextPipeline != null) {
            processLogEntry.nextCycle()
            nextPipeline = nextPipeline.run(cpuState, trackingMemory, operationState)
        }
    }

    fun printLog() {
        processLog.toList().forEach {
            println("programCounter:${it.savedCpuState.programCounter.toHex()}")
            println(it.savedCpuState)
            println(it.cycles)
        }
    }

    inner class TrackingMemory(val memory: Memory, val processLogEntry: ProcessLogEntry) : Memory {

        override fun get(position: Int): UByte {
            return memory[position].also {
                processLogEntry.log(Activity.MemoryReadActivity(position, it))
            }
        }

        override fun set(position: Int, value: UByte) {
            memory.set(position, value)
            processLogEntry.log(Activity.MemoryWriteActivity(position, value))
        }
    }

    inner class ProcessLogEntry(cpuState: CpuState) {
        val savedCpuState: CpuState = cpuState.copy()
        val cycles: MutableList<MutableList<Activity>> = mutableListOf()

        fun nextCycle() {
            cycles.add(mutableListOf())
        }

        fun log(activity: Activity) {
            cycles.last().add(activity)
        }
    }

    inner class ProcessLog(val maxSize: Int) {
        private val storage = arrayOfNulls<ProcessLogEntry>(maxSize)
        private var nextPosition = 0

        fun add(processLogEntry: ProcessLogEntry) {
            storage[nextPosition++] = processLogEntry
            if (nextPosition >= maxSize) {
                nextPosition = 0
            }
        }

        fun toList(): List<ProcessLogEntry> {
            val positions = 0 until maxSize
            val first = positions.take(nextPosition).reversed()
            val last = positions.drop(nextPosition).reversed()
            return first.plus(last).reversed().mapNotNull {
                storage[it]
            }
        }
    }
}









