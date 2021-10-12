package net.chompsoftware.knes

import net.chompsoftware.knes.hardware.*
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class HardwareInterrogator(private val cpuState: CpuState, private val memory: Memory) {

    val cycleLog: MutableList<MutableList<Activity>> = mutableListOf()

    val trackingMemory: Memory by lazy {
        TrackingMemory(memory)
    }

    fun processInstruction() {
        val effectState = EffectState(0, null, null, null)
        nextCycle()
        var nextPipeline: EffectPipeline? = InstructionCheckEffectPipeline.run(cpuState, trackingMemory, effectState)

        while (nextPipeline != null) {
            nextCycle()
            nextPipeline = nextPipeline.run(cpuState, trackingMemory, effectState)
        }
    }

    fun log(activity: Activity) {
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
            TODO("Not yet implemented")
        }
    }

    fun assertCycleLog(init: CycleLog.() -> Unit) {
        val expected = CycleLog()
        expected.init()
        val expectedCycleLog:List<List<Activity>> = expected.build()
        assertEquals(expectedCycleLog, cycleLog)
    }

    fun assertCpuState(init: AssertCpuState.() -> Unit) {
        val assertions = AssertCpuState()
        assertions.init()
    }

    inner class AssertCpuState {
        fun assertProgramCounter(expected:Int) {
            assertEquals(expected, cpuState.programCounter,"assertProgramCounter")
        }

        fun assertAReg(expected: UByte) {
            assertEquals(expected, cpuState.aReg, "assertAReg")
        }
    }

}

@ExperimentalUnsignedTypes
sealed class Activity {
    data class MemoryReadActivity(val position: Int, val returned: UByte) : Activity()
    data class MemoryWriteActivity(val position: Int, val set: UByte) : Activity()
}

@ExperimentalUnsignedTypes
class CycleLog {
    val cycles = arrayListOf<Cycle>()

    fun cycle(init: Cycle.() -> Unit): Cycle {
        val cycle = Cycle()
        cycle.init()
        cycles.add(cycle)
        return cycle
    }

    fun build() = cycles.map { it.activities }
}

@ExperimentalUnsignedTypes
class Cycle {
    val activities = arrayListOf<Activity>()

    fun activity(act: Activity) {
        activities.add(act)
    }
}









