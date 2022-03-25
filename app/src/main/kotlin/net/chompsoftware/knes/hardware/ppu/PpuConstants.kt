package net.chompsoftware.knes.hardware.ppu

// These are all in the 0x200* range in CpuMemory
const val PPU_REG_CONTROLLER = 0x0
const val PPU_REG_MASK = 0x1
const val PPU_REG_STATUS = 0x2
const val PPU_REG_OAM_ADDRESS = 0x3
const val PPU_REG_OAM_DATA = 0x4
const val PPU_REG_SCROLL = 0x5
const val PPU_REG_ADDRESS = 0x6
const val PPU_REG_DATA = 0x7


const val PPU_REG_OAM_DMA = 0x4014


const val PPU_SCANLINE_SIZE = 341
const val PPU_SCANLINE_FRAME = 262
const val PPU_SCANLINE_NMI_INTERRUPT = 241
