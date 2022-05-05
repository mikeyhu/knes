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

    fun error(functionToMessage: () -> String) {
        if (loggingEnabled && logError()) log.println(functionToMessage())
    }

    fun warn(functionToMessage: () -> String) {
        if (loggingEnabled && logWarn()) log.println(functionToMessage())
    }

    fun debug(functionToMessage: () -> String) {
        if (loggingEnabled && logDebug()) log.println(functionToMessage())
    }

    fun verbose(functionToMessage: () -> String) {
        if (loggingEnabled && logVerbose()) log.println(functionToMessage())
    }

    private fun logError() = logLevel > 0
    private fun logWarn() = logLevel > 1
    private fun logVerbose() = logLevel > 2
    private fun logDebug() = logLevel > 3
}