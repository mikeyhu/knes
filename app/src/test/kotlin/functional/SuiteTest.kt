package functional

import net.chompsoftware.knes.HardwareLogger
import net.chompsoftware.knes.hardware.*
import net.chompsoftware.knes.toHex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import java.io.File

@ExperimentalUnsignedTypes
class SuiteTest {

    @EnabledIfEnvironmentVariable(named = "SUITE", matches = "true")
    @Test
    fun `Run external suite`() {
        val suiteFile = readFileToByteArray("../externalSuite/6502_functional_test.bin")
        val memory = BasicMemory(suiteFile)

        val cpuState = CpuState(
            programCounter = 0x400
        )

        var operationsDone = 0
        val start = System.nanoTime()

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}")
        }

        val reportThenFail = { message: String ->
            report()
            fail(message)
        }

        val hardware = HardwareLogger(cpuState, memory)

        do {
            val counter = cpuState.programCounter
            try {
                val operationState = OperationState(0)
                hardware.processInstruction(operationState)
            } catch (error: Error) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            }
            operationsDone++
            if (counter == cpuState.programCounter) {
                println(cpuState)
                reportThenFail("hit trap at ${counter.toHex()}")
            }
        } while (counter != cpuState.programCounter)

        report()
    }

    fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()
}