package net.chompsoftware.knes

import java.io.File
import java.io.PrintWriter

const val logfileName = "/tmp/knes.log"

object Logging {
    lateinit var log: PrintWriter

    private var loggingEnabled = false
    private var logLevel = 0

    fun enableLogging(level: Int, toFile: Boolean) {
        logLevel = level
        if (logLevel > 0) {
            log = if (toFile) {
                File(logfileName).delete()
                File(logfileName).printWriter()
            } else {
                PrintWriter(System.out, true)
            }

        }
        loggingEnabled = true
    }

    fun error(message: String) {
        if (loggingEnabled && logError()) log.println(message)
    }

    fun error(error: Throwable) {
        if (loggingEnabled && logError()) {
            log.println("Logging an exception: $error")
            error.printStackTrace()
        }
    }

    fun warn(message: String) {
        if (loggingEnabled && logWarn()) log.println(message)
    }

    fun debug(message: String) {
        if (loggingEnabled && logDebug()) log.println(message)
    }

    fun verbose(message: String) {
        if (loggingEnabled && logVerbose()) log.println(message)
    }

    private fun logError() = logLevel > 0
    private fun logWarn() = logLevel > 1
    private fun logVerbose() = logLevel > 2
    private fun logDebug() = logLevel > 3
}