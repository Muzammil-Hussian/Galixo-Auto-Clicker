package com.galixo.autoClicker.feature.config.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.core.content.ContextCompat
import com.galixo.autoClicker.core.common.base.extensions.WindowManagerCompat
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.ClickPointViewBinding
import com.galixo.autoClicker.feature.config.ui.view.touchListener.ViewTouchEventHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CreateSwipeView(
    private val context: Context,
    private val windowManager: WindowManager,
    val swipeDescription: Action.Swipe,
    private val onPositionChanged: (Action.Swipe) -> Unit,
    private val onPointClick: (viewType: String, position: PointF) -> Unit
) {

    private lateinit var fromViewBinding: ClickPointViewBinding
    private lateinit var toViewBinding: ClickPointViewBinding

    private val fromPosition = swipeDescription.fromPosition
    private val toPosition = swipeDescription.toPosition

    private val postHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private val debounceDuration = 600L

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.overlayViewPrimaryBackground)
        strokeWidth = 8f
    }

    private lateinit var drawingView: View
    private lateinit var fromParams: LayoutParams
    private lateinit var toParams: LayoutParams

    companion object {
        private const val TAG = "CreateSwipeView"
    }

    init {
        addSwipeTarget()
    }

    suspend fun remove() = withContext(Dispatchers.Main) {
        windowManager.removeView(fromViewBinding.root)
        windowManager.removeView(toViewBinding.root)
        windowManager.removeView(drawingView)
    }

    private fun addSwipeTarget() {
        fromViewBinding = createTargetView(fromPosition, "fromView") { position ->
            swipeDescription.fromPosition = position
        }
        toViewBinding = createTargetView(toPosition, "toView") { position ->
            swipeDescription.toPosition = position
        }
        inflateDrawingView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createTargetView(position: Point, viewType: String, updateDescription: (Point) -> Unit): ClickPointViewBinding {
        val binding = ClickPointViewBinding.inflate(LayoutInflater.from(context), null, false)

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            WindowManagerCompat.TYPE_COMPAT_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE
                    or LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = position.x
            y = position.y
        }

        if (viewType == "fromView") fromParams = params else toParams = params

        var initialX = 0f
        var initialY = 0f

        val viewTouchEventHandler = ViewTouchEventHandler { newPosition ->
            position.set(newPosition.x, newPosition.y)
            updateDescription(position)
            windowManager.updateViewLayout(binding.root, params.apply {
                x = newPosition.x
                y = newPosition.y
            })
            drawLine()
        }

        /*    with(binding) {
                clickIndex.text = swipeDescription.priority.toString()
                root.apply {
                    setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                initialX = event.rawX
                                initialY = event.rawY
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
                                    v.performClick()
                                }
                                onPositionChanged.invoke(swipeDescription)
                                true
                            }

                            else -> false
                        }
                    }
                    setOnClickListener {
                        Log.d(TAG, "Clicked on $viewType")
                        onPointClick.invoke(viewType, PointF(position.x.toFloat(), position.y.toFloat()))
                    }
                }
            }*/
        with(binding) {
            val indexedValue = (swipeDescription.priority + 1).toString()
            clickIndex.text = indexedValue
            root.apply {
                setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = event.rawX
                            initialY = event.rawY
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

                            if (distanceMoved < TAP_THRESHOLD && isClickAllowed) {
                                isClickAllowed = false
                                postHandler.postDelayed({ isClickAllowed = true }, debounceDuration)
                                onPointClick.invoke(viewType, PointF(position.x.toFloat(), position.y.toFloat()))
                            }
                            onPositionChanged.invoke(swipeDescription)
                            true
                        }

                        else -> false
                    }
                }
                setOnClickListener {
                    if (isClickAllowed) {
                        isClickAllowed = false
                        postHandler.postDelayed({ isClickAllowed = true }, debounceDuration)
                        Log.d(TAG, "Clicked on $viewType")
                        onPointClick.invoke(viewType, PointF(position.x.toFloat(), position.y.toFloat()))
                    }
                }
            }
        }
        windowManager.addView(binding.root, params)
        return binding
    }

    private fun inflateDrawingView() {
        drawingView = object : View(context) {
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)

                val viewWidth = fromViewBinding.root.width.toFloat()
                val viewHeight = fromViewBinding.root.height.toFloat()
                val circleRadius = viewWidth / 2

                val fromCenterX = fromPosition.x.toFloat() + circleRadius
                val fromCenterY = fromPosition.y.toFloat() + circleRadius
                val toCenterX = toPosition.x.toFloat() + circleRadius
                val toCenterY = toPosition.y.toFloat() + circleRadius

                val angle = Math.toDegrees(atan2((toCenterY - fromCenterY), (toCenterX - fromCenterX)).toDouble()).toFloat()
                val directionVectorX = (toCenterX - fromCenterX) / sqrt((toCenterX - fromCenterX).pow(2) + (toCenterY - fromCenterY).pow(2))
                val directionVectorY = (toCenterY - fromCenterY) / sqrt((toCenterX - fromCenterX).pow(2) + (toCenterY - fromCenterY).pow(2))

                val extendedFromX = fromCenterX - directionVectorX * circleRadius
                val extendedFromY = fromCenterY - directionVectorY * circleRadius
                val extendedToX = toCenterX + directionVectorX * circleRadius
                val extendedToY = toCenterY + directionVectorY * circleRadius

                val distance = sqrt((extendedToX - extendedFromX).pow(2) + (extendedToY - extendedFromY).pow(2))

                canvas.save()
                canvas.translate((extendedFromX + extendedToX) / 2, (extendedFromY + extendedToY) / 2)
                canvas.rotate(angle)

                val cornerRadius = 80f

                canvas.drawRoundRect(
                    -distance / 2, -viewHeight / 2,
                    distance / 2, viewHeight / 2,
                    cornerRadius, cornerRadius,
                    paint
                )
                canvas.restore()

                positionArrowOnLine(extendedFromX, extendedFromY, angle, circleRadius, true, canvas)
                positionArrowOnLine(extendedToX, extendedToY, angle, circleRadius, false, canvas)
            }

            private fun positionArrowOnLine(centerX: Float, centerY: Float, angle: Float, circleRadius: Float, isFromView: Boolean, canvas: Canvas) {
                val arrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_swipe_arrows)
                val arrowSize = 50
                val margin = 30 * context.resources.displayMetrics.density

                val offset = if (isFromView) circleRadius + margin else -(circleRadius + margin)

                val offsetX = (offset * cos(Math.toRadians(angle.toDouble()))).toFloat()
                val offsetY = (offset * sin(Math.toRadians(angle.toDouble()))).toFloat()

                canvas.save()
                canvas.translate(centerX + offsetX, centerY + offsetY)
                canvas.rotate(angle)

                arrowDrawable?.setBounds(
                    -arrowSize / 2,
                    -arrowSize / 2,
                    arrowSize / 2,
                    arrowSize / 2
                )
                arrowDrawable?.draw(canvas)
                canvas.restore()
            }
        }

        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            WindowManagerCompat.TYPE_COMPAT_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCHABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        windowManager.addView(drawingView, params)
    }

    private fun drawLine() {
        drawingView.invalidate()
    }

    fun toggleTouch(isTouchable: Boolean) {
        val flag = LayoutParams.FLAG_NOT_TOUCHABLE
        fromParams.flags = if (isTouchable) fromParams.flags and flag.inv() else fromParams.flags or flag
        toParams.flags = if (isTouchable) toParams.flags and flag.inv() else toParams.flags or flag

        windowManager.updateViewLayout(fromViewBinding.root, fromParams)
        windowManager.updateViewLayout(toViewBinding.root, toParams)
    }
}
