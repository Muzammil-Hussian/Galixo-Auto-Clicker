package com.galixo.autoClicker.feature.config.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.galixo.autoClicker.core.common.base.extensions.WindowManagerCompat
import com.galixo.autoClicker.core.common.display.DisplayConfigManager
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.feature.config.databinding.ClickPointViewBinding
import com.galixo.autoClicker.feature.config.ui.view.touchListener.ViewTouchEventHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class ClickPointView(
    private val context: Context,
    private val windowManager: WindowManager,
    private val displayConfigManager: DisplayConfigManager,
    val clickDescription: Action.Click,
    val onViewMoved: (Point) -> Unit,
    private val onClickView: () -> Unit
) {
    private lateinit var binding: ClickPointViewBinding

    private val position = clickDescription.position
    private val postHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val debounceDuration = 600L

    private val params = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
        WindowManagerCompat.TYPE_COMPAT_OVERLAY,
        LayoutParams.FLAG_NOT_FOCUSABLE or
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.START or Gravity.TOP
        x = displayConfigManager.displayConfig.sizePx.x
        y = displayConfigManager.displayConfig.sizePx.y
    }

    init {
        setupTargetView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTargetView() {
        binding = ClickPointViewBinding.inflate(LayoutInflater.from(context), null, false)

        params.apply {
            gravity = Gravity.START or Gravity.TOP
            x = position.x
            y = position.y
        }

        var initialX = 0f
        var initialY = 0f
        var moved = false

        val viewTouchEventHandler = ViewTouchEventHandler { newPosition ->
            position.set(newPosition.x, newPosition.y)
            windowManager.updateViewLayout(binding.root, params.apply {
                x = newPosition.x
                y = newPosition.y
            })
            moved = true
        }

        with(binding) {
            clickIndex.text = clickDescription.priority.toString()
            root.apply {
                setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = event.rawX
                            initialY = event.rawY
                            moved = false
                            viewTouchEventHandler.onTouchEvent(v, event)
                            true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            viewTouchEventHandler.onTouchEvent(v, event)
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            val deltaX = (event.rawX - initialX).toInt()
                            val deltaY = (event.rawY - initialY).toInt()
                            val distanceMoved = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())

                            if (distanceMoved < TAP_THRESHOLD) {
                                if (isClickAllowed) {
                                    isClickAllowed = false
                                    postHandler.postDelayed({ isClickAllowed = true }, debounceDuration)
                                    onClickView()
                                }
                            } else if (moved) {
                                onViewMoved(position)
                                clickDescription.position = position
                            }
                            true
                        }

                        else -> false
                    }
                }
            }
        }
        windowManager.addView(binding.root, params)
    }

    fun toggleTouch(isTouchable: Boolean) {
        params.flags = (if (isTouchable) params.flags and LayoutParams.FLAG_NOT_TOUCHABLE.inv() else params.flags or LayoutParams.FLAG_NOT_TOUCHABLE)
        windowManager.updateViewLayout(binding.root, params)
    }

    suspend fun remove() = withContext(Dispatchers.Main) { windowManager.removeView(binding.root) }
}

const val TAP_THRESHOLD = 1
