package net.chompsoftware.knes.hardware

@ExperimentalUnsignedTypes
class NesMemory() : Memory {
    val ram = UByteArray(0x800)
    val ppu = UByteArray(0x8)

    override fun get(position: Int): UByte {
        return when (position) {
            in 0 until 0x2000 -> ram[mapToRam(position)]
            in 0x2000 until 0x4000 -> ppu[mapToPPU(position)]
            else -> throw Error("NesMemory: Out of Range!")
        }
    }

    override fun set(position: Int, value: UByte) {
        return when (position) {
            in 0 until 0x2000 -> ram[mapToRam(position)] = value
            in 0x2000 until 0x4000 -> ppu[mapToPPU(position)] = value
            else -> throw Error("NesMemory: Out of Range!")
        }
    }

    private fun mapToRam(position: Int): Int {
        return position % 0x800
    }

    private fun mapToPPU(position: Int): Int {
        return position % 0x8
    }

}