package net.chompsoftware.knes.hardware

data class CpuState(
    var programCounter: Int = 0,
    var aReg: UInt = 0x0u,
    var xReg: UInt = 0x0u,
    var isNegativeFlag: Boolean = false
) {
    fun programCounterWithIncrement(): Int {
        return programCounter++
    }
}



