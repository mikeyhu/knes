package net.chompsoftware.knes.hardware


class CycleCoordinator {

    /*
    accept:
      ppu, ppumemory romloader
      cpuState, memory, operationState - needed for loading / saving state?
      pending interrupts?
     */



    fun cycle() {
        /*
            fun processInstruction(cpuState: CpuState, memory: BasicMemory, operationState: OperationState) {
                var nextPipeline: EffectPipeline? = Operation.run(cpuState, memory, operationState)
                while (nextPipeline != null) {
                    nextPipeline = nextPipeline.run(cpuState, memory, operationState)
                }
            }

            CPU:
            if an instruction is already in progress continue it.
            if not, check for any interrupts (could be more than 1?!) and start that
            otherwise use Operation.run to start next instruction

            PPU:
            increment ppu.cpuTick (naming?) get info on NMI interrupt
            set it if required

            APU:
            TODO("Not started")
         */

    }
}