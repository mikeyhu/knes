package net.chompsoftware.knes.hardware.rom

import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipInputStream


class ZipInspector {
    val inspector = RomInspector

    fun inspectZipFile(file: File) {
        try {
            val zis = ZipInputStream(FileInputStream(file))

            val zipEntry = zis.nextEntry
            if (zipEntry?.name?.endsWith("nes") == true) {

                val byteArray = zis.readBytes()
                val inspection = inspector.inspectRom(byteArray.toUByteArray())
                if (
                    inspection.romType == RomType.NES_1 &&
                    !inspection.verticalMirroring &&
                    inspection.mapper == 0
                ) {
                    println("$file is likely to be supported")
                }
            }
            zis.closeEntry()
            zis.close()
        } catch (e: Exception) {
            println("Failed to process file: $e")
        }
    }
}

fun readZipToUByteArray(file: File): UByteArray {
    ZipInputStream(FileInputStream(file)).use { zis ->
        val zipEntry = zis.nextEntry
        if (zipEntry?.name?.endsWith("nes") == true) {
            return zis.readBytes().toUByteArray()
        } else TODO(".nes file must be the only thing in the zip for now")
    }
}

// go through all the zip files and see which ones are supported
fun main() {
    val zipInspector = ZipInspector()
    Files.walk(Paths.get("../../emulation/nes/"))
        .filter { Files.isRegularFile(it) }
        .map { it.toFile() }
        .filter { it.name.endsWith(".zip") }
        .forEach { zipInspector.inspectZipFile(it) }
}

