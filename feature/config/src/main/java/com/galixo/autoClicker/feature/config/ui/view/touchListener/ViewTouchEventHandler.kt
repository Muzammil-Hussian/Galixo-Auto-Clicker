package com.galixo.autoClicker.feature.config.ui.view.touchListener

import android.graphics.Point
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

class ViewTouchEventHandler(
    private val onViewMoved: (Point) -> Unit,
) {

    private var moveInitialViewPosition: Point = Point(0, 0)

    private var moveInitialTouchPosition: Point = Point(0, 0)

    fun onTouchEvent(viewToMove: View, event: MotionEvent): Boolean =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewToMove.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onDownEvent(viewToMove, event)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                onMoveEvent(event)
                true
            }

            else -> false
        }

    private fun onDownEvent(viewToMove: View, event: MotionEvent) {
        val layoutParams = (viewToMove.layoutParams as WindowManager.LayoutParams)
        moveInitialViewPosition = Point(layoutParams.x, layoutParams.y)
        moveInitialTouchPosition = Point(event.rawX.toInt(), event.rawY.toInt())
    }

    private fun onMoveEvent(event: MotionEvent) {
        onViewMoved(
            Point(
                moveInitialViewPosition.x + (event.rawX.toInt() - moveInitialTouchPosition.x),
                moveInitialViewPosition.y + (event.rawY.toInt() - moveInitialTouchPosition.y),
            )
        )
    }
}