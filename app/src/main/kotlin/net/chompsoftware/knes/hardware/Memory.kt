package net.chompsoftware.knes.hardware



@ExperimentalUnsignedTypes
class Memory(val store: UByteArray)  {
    operator fun get(position: Int) = store[position]
    operator fun set(position: Int, value: UByte) {
        store[position] = value
    }
}