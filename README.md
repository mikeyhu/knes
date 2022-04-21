# KNES, a NES emulator written in Kotlin

## Overview
This is a non-functioning NES emulator written in Kotlin.

### Things working
* MOS 6502 official instructions - these are all working and well tested
* iNES type 1 ROM loader (used for some of the suite tests)

### Things still to do
* 6502 unofficial instructions - some of these are implemented
* Memory mirroring
* More complex ROM types
* APU
* Scrolling sprites
* Scrolling backgrounds
* PpuOperationState changing pattern tables
* 8*16 sprites

## Useful Links
This is a list of information I found useful while researching how to make this:
* https://github.com/mikeyhu/k6502 - my own BBC emulator that also contains a MOS 6502.
* http://www.6502.org/tools/emu/ - information about other emulators and test suites.
* https://github.com/Klaus2m5/6502_65C02_functional_tests/tree/master - a very well documented test suite for the CPU.
* https://www.masswerk.at/6502/6502_instruction_set.html - information on the 6502 instruction set.  
* https://bugzmanov.github.io/nes_ebook/chapter_1.html - building a NES emulator in Rust.
* https://yizhang82.dev/nes-emu-cpu - building an NES emulator

