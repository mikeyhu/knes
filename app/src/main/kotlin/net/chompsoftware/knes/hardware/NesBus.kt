package net.chompsoftware.knes.hardware

interface Bus {
    fun ppuRegisterWrite(position: Int, value: UByte)
    fun ppuRegisterRead(position: Int): UByte
}

class NesBus : Bus {
    override fun ppuRegisterWrite(position: Int, value: UByte) {
        TODO("Bus Write not yet implemented")
    }

    override fun ppuRegisterRead(position: Int): UByte {
        TODO("Bus Read not yet implemented")
    }
}