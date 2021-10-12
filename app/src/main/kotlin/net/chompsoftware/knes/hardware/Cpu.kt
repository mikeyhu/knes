package net.chompsoftware.knes.hardware

class CpuState(var programCounter: Int, var aReg: UByte) {
    fun programCounterWithIncrement():Int {
        return programCounter++
    }
}



