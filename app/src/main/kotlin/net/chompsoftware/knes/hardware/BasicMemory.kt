package net.chompsoftware.knes.hardware


interface Memory {
    operator fun get(position: Int): UByte
    operator fun set(position: Int, value: UByte): Unit
}

class BasicMemory(val store: UByteArray) : Memory {
    override operator fun get(position: Int) = store[position]
    override operator fun set(position: Int, value: UByte) {
        store[position] = value
    }
}