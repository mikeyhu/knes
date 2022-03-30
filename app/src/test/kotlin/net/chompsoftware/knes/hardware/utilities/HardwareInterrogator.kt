package net.chompsoftware.knes.hardware.utilities

import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.toHex
import org.junit.jupiter.api.Assertions.assertEquals



class HardwareInterrogator(private val cpuState: CpuState, private val memory: Memory) {

    private val cycleLog: MutableList<MutableList<Activity>> = mutableListOf()

    private val priorCpuState = cpuState.copy()

    private val trackingMemory: Memory by lazy {
        TrackingMemory(memory)
    }

    fun processInstruction() {
        val operationState = OperationState(0, null, null, null)
        nextCycle()
        var nextPipeline: EffectPipeline? = Operation.run(cpuState, trackingMemory, operationState)

        while (nextPipeline != null) {
            nextCycle()
            nextPipeline = nextPipeline.run(cpuState, trackingMemory, operationState)
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
            memory.set(position, value)
            log(Activity.MemoryWriteActivity(position, value))
        }
    }

    fun assertCycleLog(init: CycleLog.() -> Unit) {
        val expected = CycleLog()
        expected.init()
        val expectedCycleLog: List<List<Activity>> = expected.build()
        assertEquals(expectedCycleLog, cycleLog)
    }

    fun assertCpuState(init: AssertCpuState.() -> Unit) {
        val assertions = AssertCpuState()
        assertions.init()
        assertions.assertNothingElseChanged()
    }

    inner class AssertCpuState {
        val checkState = priorCpuState.copy()

        fun programCounter(expected: Int) {
            assertEquals(expected, cpuState.programCounter, "assertProgramCounter")
            checkState.programCounter = expected
        }

        fun aReg(expected: UByte) {
            assertEquals(expected, cpuState.aReg, "assertAReg")
            checkState.aReg = expected
        }

        fun xReg(expected: UByte) {
            assertEquals(expected, cpuState.xReg, "assertXReg")
            checkState.xReg = expected
        }

        fun yReg(expected: UByte) {
            assertEquals(expected, cpuState.yReg, "assertYReg")
            checkState.yReg = expected
        }

        fun stackReg(expected: UByte) {
            assertEquals(expected, cpuState.stackReg, "assertStackPointer")
            checkState.stackReg = expected
        }

        fun isNegativeFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isNegativeFlag, "assertIsNegativeFlag")
            checkState.isNegativeFlag = expected
        }


        fun isOverflowFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isOverflowFlag, "assertIsOverflowFlag")
            checkState.isOverflowFlag = expected
        }

        fun isZeroFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isZeroFlag, "assertIsZeroFlag")
            checkState.isZeroFlag = expected
        }

        fun isCarryFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isCarryFlag, "assertIsCarryFlag")
            checkState.isCarryFlag = expected
        }

        fun isDecimalFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isDecimalFlag, "assertIsDecimalFlag")
            checkState.isDecimalFlag = expected
        }

        fun isBreakCommandFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isBreakCommandFlag, "assertIsBreakCommandFlag")
            checkState.isBreakCommandFlag = expected
        }

        fun isInterruptDisabledFlag(expected: Boolean) {
            assertEquals(expected, cpuState.isInterruptDisabledFlag, "assertIsInterruptDisabledFlag")
            checkState.isInterruptDisabledFlag = expected
        }

        fun isNMIInterrupt(expected: Boolean) {
            assertEquals(expected, cpuState.isNMIInterrupt, "assertIsNMIInterrupt")
            checkState.isNMIInterrupt = expected
        }
        fun assertNothingElseChanged() {
            assertEquals(checkState, cpuState, "Unexpected CpuState change occurred,")
        }
    }

}


sealed class Activity {
    data class MemoryReadActivity(val position: Int, val returned: UByte) : Activity() {
        override fun toString() = "MemoryReadActivity(position=${position.toHex()}, returned=${returned.toHex()})"
    }

    data class MemoryWriteActivity(val position: Int, val set: UByte) : Activity() {
        override fun toString() = "MemoryWriteActivity(position=${position.toHex()}, set=${set.toHex()})"
    }
}


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


class Cycle {
    val activities = arrayListOf<Activity>()

    fun memoryRead(position: Int, returned: UByte) {
        activities.add(Activity.MemoryReadActivity(position, returned))
    }

    fun memoryWrite(position: Int, written: UByte) {
        activities.add(Activity.MemoryWriteActivity(position, written))
    }
}









