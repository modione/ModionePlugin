package me.modione.modioneplugin.utils

import kotlin.math.roundToInt

class Lag : Runnable {
    override fun run() {
        TICKS[TICK_COUNT % TICKS.size] = System.currentTimeMillis()
        TICK_COUNT += 1
    }

    companion object {
        var TICK_COUNT = 0
        var TICKS = LongArray(600)
        var LAST_TICK = 0L
        val tps: Double
            get() = getTPS(100)

        private fun getTPS(ticks: Int): Double {
            if (TICK_COUNT < ticks) {
                return 20.0
            }
            val target = (TICK_COUNT - 1 - ticks) % TICKS.size
            val elapsed = System.currentTimeMillis() - TICKS[target]
            return ((ticks / (elapsed / 1000.0)) * 10).roundToInt() / 10.0
        }
    }
}
