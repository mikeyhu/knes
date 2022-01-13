package functional

import net.chompsoftware.knes.hardware.utilities.LoggingHarness
import net.chompsoftware.knes.hardware.BasicMemory
import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.toHex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import java.io.File

class SuiteTest {

    @EnabledIfEnvironmentVariable(named = "SUITE", matches = "true")
    @Test
    fun `Run external suite`() {
        val suiteFile = readFileToByteArray("../externalSuite/6502_functional_test.bin")
        val memory = BasicMemory(suiteFile)

        val cpuState = CpuState(
            programCounter = 0x400,
            breakLocation = 0xfffe
        )

        var operationsDone = 0
        val start = System.nanoTime()

        val harness = LoggingHarness(cpuState, memory, maxSize = 10)

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}")
        }

        val reportThenFail = { message: String ->
            report()
//            harness.printLog()
            fail(message)
        }

        do {
            val counter = cpuState.programCounter
            try {
                val operationState = OperationState(0)
                harness.processInstruction(operationState)
            } catch (error: Error) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            }
            operationsDone++
            if (counter == cpuState.programCounter) {
                reportThenFail("hit trap at ${counter.toHex()}")
            }
        } while (counter != cpuState.programCounter)

        report()
    }

    fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()
}