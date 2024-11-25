package com.galixo.autoClicker.core.scenarios.domain.model

interface Repeatable {
    val repeatCount: Int
    val isRepeatInfinite: Boolean

    fun isRepeatCountValid(): Boolean =
        repeatCount > 0
}

interface RepeatableWithDelay : Repeatable {
    val repeatDelayMs: Long

    fun isRepeatDelayValid(): Boolean = repeatDelayMs > 40
}

const val REPEAT_COUNT_MIN_VALUE: Int = 1
const val REPEAT_COUNT_MAX_VALUE: Int = 99999

const val REPEAT_DELAY_MIN_MS: Long = 0
const val REPEAT_DELAY_MAX_MS: Long = 3_600_000