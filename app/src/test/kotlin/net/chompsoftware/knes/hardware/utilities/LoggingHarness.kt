package net.chompsoftware.knes.hardware.utilities

import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toLogHex
import java.io.File
import java.io.PrintWriter


class LoggingHarness(private val cpuState: CpuState, private val memory: Memory, maxSize: Int = 10) {

    private val logFileName = "/tmp/knes.log"
    private val processLog = ProcessLog(maxSize)
    var operationsDone = 0
    var cyclesDone = 7 // start at 7 due to initial bootstrapping?
    var loggingType: LoggingType? = null

    lateinit var log: PrintWriter

    private var loggingEnabled = false

    fun enableLogging(filenameOverride: String? = null, loggingType: LoggingType = LoggingType.CYCLE) {
        this.loggingType = loggingType
        val fileToUse = filenameOverride ?: logFileName
        File(fileToUse).delete()
        log = File(fileToUse).printWriter()

        loggingEnabled = true
    }

    fun finishLogging() {
        if (loggingEnabled) {
            log.close()
        }
    }

    fun processInstruction(operationState: OperationState = OperationState(0, null, null, null)) {
        val processLogEntry = ProcessLogEntry(cpuState, cyclesDone)

        val trackingMemory = TrackingMemory(memory, processLogEntry)

        processLog.add(processLogEntry)

        processLogEntry.nextCycle()
        var nextPipeline: EffectPipeline? = Operation.run(cpuState, trackingMemory, operationState)
        cyclesDone++
        while (nextPipeline != null) {
            processLogEntry.nextCycle()
            nextPipeline = nextPipeline.run(cpuState, trackingMemory, operationState)
            cyclesDone++
        }
        operationsDone++
        if (loggingEnabled) {
            when (loggingType) {
                LoggingType.CYCLE -> log.println("${processLogEntry.savedCpuState.programCounter.toLogHex()} CYC:${processLogEntry.cyclesDone}")
                LoggingType.MEMORY -> log.println("${processLogEntry.savedCpuState.programCounter.toLogHex()}  ${processLogEntry.savedCpuState}  ${processLogEntry.cycles}  CYC:${processLogEntry.cyclesDone}")
            }
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

    inner class ProcessLogEntry(cpuState: CpuState, val cyclesDone: Int) {
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

enum class LoggingType {
    CYCLE,
    MEMORY
}









