package net.chompsoftware.knes.hardware

import net.chompsoftware.knes.setupMemory
import kotlin.test.Test
import kotlin.test.assertNotNull


@ExperimentalUnsignedTypes
class OperationsTest {
    @Test fun loadAccumulator() {
        val memory = setupMemory(LDA_I,0x00u)

    }
}


