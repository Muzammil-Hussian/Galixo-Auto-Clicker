package com.galixo.autoClicker.core.scenarios.domain.model

import android.graphics.Point
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.common.base.interfaces.Identifiable

sealed class Action : Identifiable {

    /** The identifier of the scenario for this action. */
    abstract val scenarioId: Identifier

    /** The name of the action. */
    abstract val name: String?

    abstract val priority: Int

    abstract fun isValid(): Boolean


    data class Click(
        override val id: Identifier,
        override val scenarioId: Identifier,
        override val name: String,
        override val priority: Int = 1,
        override val repeatCount: Int,
        override val isRepeatInfinite: Boolean,
        override val repeatDelayMs: Long,
        var position: Point,
        val pressDurationMs: Long,
    ) : Action(), RepeatableWithDelay {

        override fun isValid(): Boolean =
            name.isNotEmpty() && pressDurationMs > 40 && isRepeatCountValid() && isRepeatDelayValid()
    }

    data class Swipe(
        override val id: Identifier,
        override val scenarioId: Identifier,
        override val name: String,
        override val priority: Int = 1,
        override val repeatCount: Int,
        override val isRepeatInfinite: Boolean,
        override val repeatDelayMs: Long,
        var fromPosition: Point,
        var toPosition: Point,
        val swipeDurationMs: Long,
    ) : Action(), RepeatableWithDelay {
        override fun isValid(): Boolean =
            name.isNotEmpty() && swipeDurationMs > 300 && isRepeatCountValid() && isRepeatDelayValid()
    }
}
