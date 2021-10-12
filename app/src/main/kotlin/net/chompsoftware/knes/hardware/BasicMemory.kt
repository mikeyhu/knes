package net.chompsoftware.knes.hardware


@ExperimentalUnsignedTypes
interface Memory {
    operator fun get(position: Int): UByte
    operator fun set(position: Int, value: UByte): Unit
}

@ExperimentalUnsignedTypes
class BasicMemory(val store: UByteArray) : Memory {
    override operator fun get(position: Int) = store[position]
    override operator fun set(position: Int, value: UByte) {
        store[position] = value
    }
}