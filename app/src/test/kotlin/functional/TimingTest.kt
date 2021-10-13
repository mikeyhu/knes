package functional

import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.toHex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail

@ExperimentalUnsignedTypes
class TimingTest {

    @EnabledIfEnvironmentVariable(named = "TIMING", matches = "true")
    @Test
    fun `Run timing`() {

        val cyclesToRun = 10000000

        val program = ubyteArrayOf(
            LDA_Z, 0x00u,
            TAX,
            INX,
            TXA,
            STA_Z, 0x00u,
            CMP_I, 0xffu,
            BNE, 0xf5u,
            LDA_I, 0x00u,
            STA_Z, 0x00u,
            BEQ, 0xefu
        )

        val memory = BasicMemory(setupProgram(program))

        val cpuState = CpuState(programCounter = 0x400)
        var operationsDone = 0L
        var cyclesDone = 0L
        val start = System.nanoTime()

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}. Ops per s: ${operationsDone / elapsed * 1000}")
            println("Cycles done: ${cyclesDone} Time taken: ${elapsed}ms. Cycles per ms: ${cyclesDone / elapsed}. Cycles per s: ${cyclesDone / elapsed * 1000}")
        }

        val reportThenFail = { message: String ->
            report()
            fail(message)
        }

        do {
            val counter = cpuState.programCounter
            try {
                val effectState = OperationState(0, null, null, null)
                var nextPipeline: EffectPipeline? = Operation.run(cpuState, memory, effectState)
                cyclesDone++
                while (nextPipeline != null) {
                    nextPipeline = nextPipeline.run(cpuState, memory, effectState)
                    cyclesDone++
                }
            } catch (error: Error) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            }
            operationsDone++
            if (counter == cpuState.programCounter) {
                reportThenFail("hit trap at ${counter.toHex()}")
            }
        } while (cyclesDone < cyclesToRun)

        report()
    }

    private fun setupProgram(program: UByteArray): UByteArray {
        val array = UByteArray(0x8000)
        program.forEachIndexed { i, ub ->
            array[0x400 + i] = ub
        }
        return array
    }
}