package com.galixo.autoClicker.core.scenarios.engine


import com.galixo.autoClicker.core.scenarios.domain.model.Repeatable
import com.galixo.autoClicker.core.scenarios.domain.model.RepeatableWithDelay
import kotlinx.coroutines.delay

internal suspend fun Repeatable.repeat(action: suspend () -> Unit): Unit =
    when {
        isRepeatInfinite -> while (true) {
            action()
            delayNextActionIfNeeded()
        }
        repeatCount > 0 -> repeat(repeatCount) {
            action()
            delayNextActionIfNeeded()
        }
        else -> Unit
    }

private suspend fun Repeatable.delayNextActionIfNeeded() {
    if (this !is RepeatableWithDelay) return
    if (repeatDelayMs == 0L) return

    delay(repeatDelayMs)
}
