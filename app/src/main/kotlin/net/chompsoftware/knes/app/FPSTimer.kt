package net.chompsoftware.knes.app

class FPSTimer(val getCurrentSecond: () -> Long = ::currentSecond) {
    private var currentSecond = getCurrentSecond()
    private var currentCounter = 0
    private var mostRecentFinished = 0

    fun mostRecent(): Int = mostRecentFinished

    fun increment() {
        val now = getCurrentSecond()
        if (now == currentSecond) {
            currentCounter++
        } else {
            mostRecentFinished = currentCounter
            currentCounter = 0
            currentSecond = now
        }
    }
}

private fun currentSecond(): Long = System.currentTimeMillis() / 1000