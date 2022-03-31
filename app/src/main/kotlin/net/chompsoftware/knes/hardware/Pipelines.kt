package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

class OperationState(
    var pipelinePosition: Int,
    var memoryValue: UByte? = null,
    var location: Int? = null,
    var argumentLow: UByte? = null,
    var argumentHigh: UByte? = null,
    var cyclesRemaining: Int = 0
) {
    fun argumentsPosition() = toInt16(getArgumentLow(), getArgumentHigh())

    fun getMemoryValue(): UByte {
        return memoryValue ?: throw Error("memoryValue was not set")
    }

    fun getLocation(): Int {
        return location ?: throw Error("location was not set")
    }

    fun getZeroPagePosition() = getArgumentLow().toInt()

    fun getZeroPagePosition(offset: UByte) = ((getArgumentLow() + offset) % 0x100u).toInt()

    fun setNextArgument(value: UByte) {
        if (argumentLow == null) argumentLow = value
        else argumentHigh = value
    }

    fun argumentsToLocation() {
        location = argumentsPosition()
    }


    fun argumentsToLocation(offset: UByte) {
        location = (argumentsPosition() + offset.toInt()) % 0x10000
    }

    fun zeroPageToLocation() {
        location = getZeroPagePosition()
    }

    fun zeroPageToLocation(offset: UByte) {
        location = getZeroPagePosition(offset)
    }

    private fun getArgumentLow(): UByte {
        return argumentLow ?: throw Error("argument1 was not set")
    }

    private fun getArgumentHigh(): UByte {
        return argumentHigh ?: throw Error("argument2 was not set")
    }

    fun reset() {
        pipelinePosition = 0
        memoryValue = null
        location = null
        argumentLow = null
        argumentHigh = null
        cyclesRemaining = 0
    }
}

interface EffectPipeline {
    fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline?
}

object Operation : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline {
        if (cpuState.isNMIInterrupt) {
            memory[cpuState.programCounter]
            cpuState.isNMIInterrupt = false
            return nmiInterruptPipeline
        }
        if (cpuState.isIRQInterrupt && !cpuState.isInterruptDisabledFlag) {
            memory[cpuState.programCounter]
            cpuState.isIRQInterrupt = false
            return irqInterruptPipeline
        }
        val instruction = memory[cpuState.programCounterWithIncrement()]

        return instructionMap.getOrElse(instruction) {
            throw NotImplementedError("Instruction ${instruction.toHex()} not found at ${(cpuState.programCounter - 1).toHex()}")
        }
    }
}

open class VariableLengthPipeline(vararg val effects: Effect) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        if (effects.size < operationState.pipelinePosition)
            throw Error("Pipeline past end of effects")
        if (operationState.cyclesRemaining > 0) {
            operationState.cyclesRemaining -= 1
            return nextEffectPipeline(operationState)
        }
        effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
        operationState.pipelinePosition++
        while (effects.size > operationState.pipelinePosition && !effects[operationState.pipelinePosition].requiresCycle()) {
            effects[operationState.pipelinePosition].run(cpuState, memory, operationState)
            operationState.pipelinePosition++
        }
        return nextEffectPipeline(operationState)
    }

    private fun nextEffectPipeline(operationState: OperationState): EffectPipeline? {
        if (effects.size > operationState.pipelinePosition || operationState.cyclesRemaining > 0)
            return this
        operationState.reset()
        return null
    }
}

class SingleEffectPipeline(val effect: Effect) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        effect.run(cpuState, memory, operationState)
        operationState.reset()
        return null
    }
}

class DelayedSingleEffectPipeline(val effect: Effect, val delay: Int) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        if (operationState.pipelinePosition < delay) {
            operationState.pipelinePosition++
            return this
        }
        effect.run(cpuState, memory, operationState)
        operationState.reset()
        return null
    }
}

class ImmediateReadPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    *postEffects
)

open class AbsoluteMemoryPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    *postEffects
)

class AbsoluteReadPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    AbsoluteRead,
    *postEffects
)

class AbsoluteXReadPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    AbsoluteReadWithXOffset,
    *postEffects
)

class AbsoluteYReadPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    AbsoluteReadWithYOffset,
    *postEffects
)

class AbsoluteLocationPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    ArgumentsToLocation,
    *postEffects
)

class AbsoluteXLocationPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    ArgumentsToLocationWithXOffset,
    *postEffects
)

class AbsoluteYLocationPipeline(vararg postEffects: Effect) : AbsoluteMemoryPipeline(
    ArgumentsToLocationWithYOffset,
    *postEffects
)

open class ZeroPagePipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    *postEffects
)

class ZeroPageReadPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageRead,
    *postEffects
)

class ZeroPageXReadPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageXRead,
    *postEffects
)

class ZeroPageYReadPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageYRead,
    *postEffects
)

class ZeroPageLocationPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageToLocation,
    *postEffects
)

class ZeroPageXLocationPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageXToLocation,
    *postEffects
)

class ZeroPageYLocationPipeline(vararg postEffects: Effect) : ZeroPagePipeline(
    ZeroPageYToLocation,
    *postEffects
)

class IndirectIndexedReadPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageToLocation,
    ReadLocationLow,
    ReadLocationZeroPageHigh,
    AbsoluteReadWithYOffset,
    *postEffects
)

class IndexedIndirectReadPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageXToLocation,
    ReadLocationLow,
    ReadLocationZeroPageHigh,
    AbsoluteRead,
    *postEffects
)

class IndirectIndexedLocationPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageToLocation,
    ReadLocationLow,
    ReadLocationZeroPageHigh,
    ArgumentsToLocationWithYOffset,
    *postEffects
)

class IndexedIndirectLocationPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageXToLocation,
    ReadLocationLow,
    ReadLocationZeroPageHigh,
    ArgumentsToLocation,
    *postEffects
)

class IndirectPipeline(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    ArgumentsToLocation,
    ReadLocationLow,
    ReadLocationHighWithWrap,
    ArgumentsToLocation,
    *postEffects
)

fun surroundWithMemoryReadWrite(vararg effects: Effect) = arrayOf(MemoryReadFromLocation, *effects, StoreMemory)