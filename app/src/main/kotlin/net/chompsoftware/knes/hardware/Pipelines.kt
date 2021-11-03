package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.hardware.effects.*
import net.chompsoftware.knes.toHex
import net.chompsoftware.knes.toInt16

class OperationState(
    var pipelinePosition: Int,
    var memoryRead: UByte? = null,
    var location: Int? = null,
    var argumentLow: UByte? = null,
    var argumentHigh: UByte? = null,
    var cyclesRemaining: Int = 0
) {
    fun argumentsPosition() = toInt16(getArgumentLow(), getArgumentHigh())

    fun getMemoryRead(): UByte {
        return memoryRead ?: throw Error("memoryRead was not set")
    }

    fun getLocation(): Int {
        return location ?: throw Error("location was not set")
    }

    fun getZeroPagePosition() = getArgumentLow().toInt()

    fun getZeroPagePosition(offset:UByte) = ((getArgumentLow() + offset) % 0x100u).toInt()

    fun setNextArgument(value: UByte) {
        if (argumentLow == null) argumentLow = value
        else argumentHigh = value
    }

    fun argumentsToLocation() {
        location = argumentsPosition()
    }


    fun argumentsToLocation(offset: UByte) {
        location = argumentsPosition() + offset.toInt()
    }

    fun zeroPageToLocation() {
        location = getZeroPagePosition()
    }

    private fun getArgumentLow(): UByte {
        return argumentLow ?: throw Error("argument1 was not set")
    }

    private fun getArgumentHigh(): UByte {
        return argumentHigh ?: throw Error("argument2 was not set")
    }

    fun reset() {
        pipelinePosition = 0
        memoryRead = null
        location = null
        argumentLow = null
        argumentHigh = null
        cyclesRemaining = 0
    }
}

@ExperimentalUnsignedTypes
interface EffectPipeline {
    fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline?
}

@ExperimentalUnsignedTypes
object Operation : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline {
        val instruction = memory[cpuState.programCounterWithIncrement()]

        return instructionMap.getOrElse(instruction) {
            throw NotImplementedError("Instruction ${instruction.toHex()} not found at ${(cpuState.programCounter - 1).toHex()}")
        }
    }
}

@ExperimentalUnsignedTypes
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

@ExperimentalUnsignedTypes
class SingleEffectPipeline(val effect: Effect) : EffectPipeline {
    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline? {
        effect.run(cpuState, memory, operationState)
        return null
    }
}

@ExperimentalUnsignedTypes
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

@ExperimentalUnsignedTypes
class ImmediateMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    AbsoluteRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteXMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    AbsoluteReadWithXOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteYMemoryReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    AbsoluteReadWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    ArgumentsToLocation,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteXMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    ArgumentsToLocationWithXOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class AbsoluteYMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    ArgumentsToLocationWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageXReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageXRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageYReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageYRead,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    ZeroPageWrite,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageXWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    ZeroPageXWrite,
    *postEffects
)

@ExperimentalUnsignedTypes
class ZeroPageYWriteOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ImmediateRead,
    ZeroPageYWrite,
    *postEffects
)

@ExperimentalUnsignedTypes
class IndirectIndexedReadOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageToLocation,
    ReadLocationLow,
    ReadLocationHigh,
    AbsoluteReadWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class IndirectIndexedMemoryLocationOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ZeroPageToLocation,
    ReadLocationLow,
    ReadLocationHigh,
    ArgumentsToLocationWithYOffset,
    *postEffects
)

@ExperimentalUnsignedTypes
class IndirectOperation(vararg postEffects: Effect) : VariableLengthPipeline(
    ReadAtProgramCounter,
    ReadAtProgramCounter,
    ArgumentsToLocation,
    ReadLocationLow,
    ReadLocationHigh,
    ArgumentsToLocation,
    *postEffects
)