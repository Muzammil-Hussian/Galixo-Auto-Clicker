package com.galixo.autoClicker.core.scenarios.engine

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import com.galixo.autoClicker.core.common.base.AndroidExecutor
import com.galixo.autoClicker.core.common.base.extensions.buildSingleStroke
import com.galixo.autoClicker.core.common.base.extensions.nextIntInOffset
import com.galixo.autoClicker.core.common.base.extensions.nextLongInOffset
import com.galixo.autoClicker.core.common.base.extensions.safeLineTo
import com.galixo.autoClicker.core.common.base.extensions.safeMoveTo
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Repeatable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random


internal class ActionExecutor(private val androidExecutor: AndroidExecutor) {

    private val random: Random = Random(System.currentTimeMillis())
    private var randomize: Boolean = false

    suspend fun executeAction(action: Action, randomize: Boolean) {
        this.randomize = randomize
        when (action) {
            is Action.Click -> executeClick(action)
            is Action.Swipe -> executeSwipe(action)
        }
    }

    private suspend fun executeClick(dumbClick: Action.Click) {
        val clickGesture = GestureDescription.Builder().buildSingleStroke(
            path = Path().apply { moveTo(dumbClick.position.x + 50, dumbClick.position.y + 50) },
            durationMs = dumbClick.pressDurationMs.randomizeDurationIfNeeded(),
        )

        executeRepeatableGesture(clickGesture, dumbClick)
    }

    private suspend fun executeSwipe(dumbSwipe: Action.Swipe) {
        val swipeGesture = GestureDescription.Builder().buildSingleStroke(
            path = Path().apply {
                moveTo(dumbSwipe.fromPosition.x, dumbSwipe.fromPosition.y + 130)
                lineTo(dumbSwipe.toPosition.x, dumbSwipe.toPosition.y + 130)
            },
            durationMs = dumbSwipe.swipeDurationMs.randomizeDurationIfNeeded(),
        )

        executeRepeatableGesture(swipeGesture, dumbSwipe)
    }


    private suspend fun executeRepeatableGesture(gesture: GestureDescription, repeatable: Repeatable) {
        repeatable.repeat {
            withContext(Dispatchers.Main) {
                androidExecutor.executeGesture(gesture)
            }
        }
    }

    private fun Path.moveTo(x: Int, y: Int) {
        if (!randomize) safeMoveTo(x, y)
        else safeMoveTo(
            random.nextIntInOffset(x, RANDOMIZATION_POSITION_MAX_OFFSET_PX),
            random.nextIntInOffset(y, RANDOMIZATION_POSITION_MAX_OFFSET_PX),
        )
    }

    private fun Path.lineTo(x: Int, y: Int) {
        if (!randomize) safeLineTo(x, y)
        else safeLineTo(
            random.nextIntInOffset(x, RANDOMIZATION_POSITION_MAX_OFFSET_PX),
            random.nextIntInOffset(y, RANDOMIZATION_POSITION_MAX_OFFSET_PX),
        )
    }

    private fun Long.randomizeDurationIfNeeded(): Long =
        if (randomize) random.nextLongInOffset(this, RANDOMIZATION_DURATION_MAX_OFFSET_MS)
        else this
}


private const val RANDOMIZATION_POSITION_MAX_OFFSET_PX = 5
private const val RANDOMIZATION_DURATION_MAX_OFFSET_MS = 5L