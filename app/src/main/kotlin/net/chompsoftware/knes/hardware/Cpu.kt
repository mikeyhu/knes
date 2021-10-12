package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.toInt16



@ExperimentalUnsignedTypes
fun loadAccumulator(state: CpuState, memory: Memory, addressing: Addressing): Sequence<Unit> {

    val toSet = when (addressing) {
        Addressing.immediate -> return sequence {
            state.aReg = memory.get(state.programCounterWithIncrement())
            yield(Unit)
        }
        Addressing.ab -> return sequence {
            val lower = memory.get(state.programCounterWithIncrement())
            yield(Unit)
            val upper = memory.get(state.programCounterWithIncrement())
            yield(Unit)
            val pos = toInt16(lower,upper)
            state.aReg = memory.get(pos)
            yield(Unit)
        }
        else -> throw NotImplementedError()
    }

}

@ExperimentalUnsignedTypes
fun processInstruction(cpuState: CpuState, memory: Memory): Sequence<Unit> {
    return sequence {
        val instruction = memory.get(cpuState.programCounterWithIncrement())
        yield(Unit)
        when (instruction) {
            LDA_I -> yieldAll(loadAccumulator(cpuState, memory, Addressing.immediate))
            LDA_AB -> yieldAll(loadAccumulator(cpuState, memory, Addressing.ab))
            else -> throw NotImplementedError()
        }
    }
}

class CpuState(var programCounter: Int, var aReg: UByte) {
    fun programCounterWithIncrement():Int {
        return programCounter++
    }
}




data class ReadFromMemoryOutcome(val value: UByte)

const val LDA_I: UByte = 0xa9u
const val LDA_AB: UByte = 0xadu

enum class Addressing(val size: Int) {
    none(1),
    immediate(2),
    iiy(2),
    iix(2),
    ir(3),
    ab(3),
    abx(3),
    aby(3),
    z(2),
    zx(2),
    zy(2)
}