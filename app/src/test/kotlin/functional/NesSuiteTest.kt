package functional

import net.chompsoftware.knes.hardware.CpuState
import net.chompsoftware.knes.hardware.NesBus
import net.chompsoftware.knes.hardware.NesMemory
import net.chompsoftware.knes.hardware.OperationState
import net.chompsoftware.knes.hardware.input.NesControllerInput
import net.chompsoftware.knes.hardware.ppu.NesPpuMemory
import net.chompsoftware.knes.hardware.ppu.NesPpu
import net.chompsoftware.knes.hardware.rom.RomLoader
import net.chompsoftware.knes.hardware.utilities.LoggingHarness
import net.chompsoftware.knes.hardware.utilities.LoggingType
import net.chompsoftware.knes.readFileToUByteArray
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.stream.Stream

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

        val suiteFile = readFileToUByteArray(File(testDirectory + file))

        val mapper = RomLoader.loadMapper(suiteFile)
        val ppu = NesPpu(NesPpuMemory(mapper))
        val memory = NesMemory(
            mapper,
            NesBus(ppu, NesControllerInput()),
            failOnReadError = false,
            failOnWriteError = false
        )

        val initialCounter = toInt16(memory[0xfffc], memory[0xfffd])

        val cpuState = CpuState(
            programCounter = initialCounter,
            breakLocation = 0xfffe
        )

        var operationsDone = 0
        val start = System.nanoTime()

        val harness = LoggingHarness(cpuState, memory, maxSize = 10)

        harness.enableLogging("/tmp/knes.${file}.log", LoggingType.MEMORY)

        val report = {
            val finish = System.nanoTime()
            val elapsed = (finish - start) / 1000000
            if (elapsed > 0) {
                println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}")
            }
            println("Suite message: \n${getSuiteMessage(memory)}")
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

    @EnabledIfEnvironmentVariable(named = "NESSUITE", matches = "true")
    @Test
    fun `Run external NES suites - CPU - nestest`() {

        val suiteFile = readFileToUByteArray(File("../nesSuite/nestest.nes"))

        val mapper = RomLoader.loadMapper(suiteFile)
        val ppu = NesPpu(NesPpuMemory(mapper))
        val memory = NesMemory(
            mapper,
            NesBus(ppu, NesControllerInput()),
            failOnReadError = false,
            failOnWriteError = false
        )

        val cpuState = CpuState(
            programCounter = 0xC000,
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

    private fun getSuiteMessage(memory: NesMemory): String {
        return sequence {
            var position = 0x6004
            while (memory[position].toUInt() != 0u) {
                yield(memory[position++].toInt().toChar())
            }
        }.joinToString("")
    }
}