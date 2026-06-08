package com.credit.bridge.util

import android.os.Bundle
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CommonCountdown(
    private val scope: CoroutineScope,
    private val onTick: (remainingSeconds: Int) -> Unit,
    private val onFinish: () -> Unit,
) {
    private var endAtElapsedRealtime: Long = 0L
    private var tickJob: Job? = null

    val isRunning: Boolean
        get() = remainingSeconds() > 0

    fun remainingSeconds(): Int {
        if (endAtElapsedRealtime <= 0L) return 0
        return ((endAtElapsedRealtime - SystemClock.elapsedRealtime()) / 1000L)
            .coerceAtLeast(0)
            .toInt()
    }

    fun start(durationSeconds: Int) {
        if (durationSeconds <= 0) {
            stop()
            onFinish()
            return
        }
        endAtElapsedRealtime = SystemClock.elapsedRealtime() + durationSeconds * 1000L
        startTicker()
        refresh()
    }

    fun startWithMillis(totalMillis: Long) {
        start((totalMillis / 1000L).coerceAtLeast(1).toInt())
    }

    fun refresh() {
        if (!isRunning) {
            if (endAtElapsedRealtime > 0L) {
                endAtElapsedRealtime = 0L
                tickJob?.cancel()
                tickJob = null
                onFinish()
            }
            return
        }
        onTick(remainingSeconds())
    }

    fun stop() {
        tickJob?.cancel()
        tickJob = null
        endAtElapsedRealtime = 0L
    }

    fun saveState(outState: Bundle, key: String) {
        if (isRunning) {
            outState.putLong(key, endAtElapsedRealtime)
        }
    }

    fun restoreState(savedState: Bundle?, key: String): Boolean {
        val end = savedState?.getLong(key, 0L) ?: 0L
        if (end <= SystemClock.elapsedRealtime()) {
            endAtElapsedRealtime = 0L
            return false
        }
        endAtElapsedRealtime = end
        startTicker()
        refresh()
        return true
    }

    private fun startTicker() {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                val remain = remainingSeconds()
                if (remain <= 0) {
                    endAtElapsedRealtime = 0L
                    onFinish()
                    break
                }
                onTick(remain)
                delay(1000)
            }
        }
    }
}
