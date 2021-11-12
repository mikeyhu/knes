package functional

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.hardware.rom.RomInspector
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.utilities.LoggingHarness
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.stream.Stream

@ExperimentalUnsignedTypes
class NesSuiteTest {

    companion object {
        @JvmStatic
        val testDirectory = "../../nes-test-roms/instr_test-v3/rom_singles/"

        @JvmStatic
        fun testFiles(): Stream<String> {
            val files = File(testDirectory).listFiles()
            val filenames = files.map { it.name }.sorted()
            return filenames.stream()
        }
    }

    @EnabledIfEnvironmentVariable(named = "NESSUITE", matches = "true")
    @ParameterizedTest
    @MethodSource("testFiles")
    fun `Run external NES suites - CPU`(file: String) {

        val suiteFile = readFileToByteArray(File(testDirectory + file))

        val romInformation = RomInspector.inspectRom(suiteFile)

        println(romInformation)

        val memory = RomLoader.loadMemory(romInformation, suiteFile)

        val initialCounter = toInt16(memory[0xfffc], memory[0xfffd])

        val cpuState = CpuState(
            programCounter = initialCounter,
            breakLocation = 0xfffe
        )

        var operationsDone = 0
        val start = System.nanoTime()

        val harness = LoggingHarness(cpuState, memory, maxSize = 10)

        val getSuiteMessage = {
            memory.store.drop(0x6004).takeWhile { it.toUInt() != 0u }.map { it.toInt().toChar() }.joinToString("")
        }

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}")
            println("Suite message: \n${getSuiteMessage()}")
        }

        val reportThenFail = { message: String ->
            report()
            harness.printLog()
            fail(message)
        }

        do {
            val counter = cpuState.programCounter
            try {
                val operationState = OperationState(0)
                harness.processInstruction(operationState)
            } catch (error: Error) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            } catch (error: Exception) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            }
            operationsDone++
            if (counter == cpuState.programCounter) {
                if (memory[0x6000].toUInt() == 0x0u) {
                    break
                } else {
                    reportThenFail("hit trap at ${counter.toHex()}")
                }
            }
        } while (counter != cpuState.programCounter)

        report()
    }

    @Test
    fun `Run external NES suites - CPU - nestest`() {

        val suiteFile = readFileToByteArray(File("../nesSuite/nestest.nes"))

        val romInformation = RomInspector.inspectRom(suiteFile)

        println(romInformation)

        val memory = RomLoader.loadMemory(romInformation, suiteFile)

        val initialCounter = 0xC000

        val cpuState = CpuState(
            programCounter = initialCounter,
            stackReg = 0xfdu,
            breakLocation = 0xfffe
        )

        val start = System.nanoTime()

        val harness = LoggingHarness(cpuState, memory, maxSize = 10)
        harness.enableLogging()

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            println("Operations done: ${harness.operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${harness.operationsDone / elapsed}")
            harness.finishLogging()
        }

        val reportThenFail = { message: String ->
            report()
            harness.printLog()
            fail(message)
        }

        do {
            val counter = cpuState.programCounter
            try {
                val operationState = OperationState(0)
                harness.processInstruction(operationState)
            } catch (error: Error) {
                reportThenFail("failed at ${counter.toHex()} with $error")
            } catch (e: Exception) {
                reportThenFail("failed at ${counter.toHex()} with ${e}")
            }
            if (counter == cpuState.programCounter) {
                reportThenFail("hit trap at ${counter.toHex()}")
            }
        } while (counter != cpuState.programCounter)

        report()
    }

    private fun readFileToByteArray(file: File) = file.inputStream().readBytes().asUByteArray()
}