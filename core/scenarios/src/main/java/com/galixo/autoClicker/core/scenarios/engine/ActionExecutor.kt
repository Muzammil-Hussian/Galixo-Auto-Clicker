package com.galixo.autoClicker.core.scenarios.engine

import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Path
import android.util.TypedValue
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
        val adjustment = 20F.dpToPx().toInt()
        val statusBarHeight = getStatusBarHeight() // Use root view dynamically
        when (action) {
            is Action.Click -> executeClick(action, adjustment)
            is Action.Swipe -> executeSwipe(action, adjustment, statusBarHeight)
        }
    }

    private suspend fun executeClick(click: Action.Click, adjustment: Int) {
        val clickGesture = GestureDescription.Builder().buildSingleStroke(
            path = Path().apply { moveTo(click.position.x + adjustment, click.position.y + adjustment) },
            durationMs = click.pressDurationMs.randomizeDurationIfNeeded(),
        )

        executeRepeatableGesture(clickGesture, click)
    }

    private suspend fun executeSwipe(swipe: Action.Swipe, adjustment: Int, statusBarHeight: Int) {

        val swipeGesture = GestureDescription.Builder().buildSingleStroke(
            path = Path().apply {
                moveTo(
                    swipe.fromPosition.x + adjustment,
                    swipe.fromPosition.y + statusBarHeight + adjustment
                )
                lineTo(
                    swipe.toPosition.x + adjustment,
                    swipe.toPosition.y + statusBarHeight + adjustment
                )
            },
            durationMs = swipe.swipeDurationMs.randomizeDurationIfNeeded(),
        )

        executeRepeatableGesture(swipeGesture, swipe)
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(): Int {
        val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) Resources.getSystem().getDimensionPixelSize(resourceId) else 0
    }

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
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


private const val RANDOMIZATION_POSITION_MAX_OFFSET_PX = 15
private const val RANDOMIZATION_DURATION_MAX_OFFSET_MS = 15L