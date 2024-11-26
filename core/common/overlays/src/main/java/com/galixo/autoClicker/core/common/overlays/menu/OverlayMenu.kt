package com.galixo.autoClicker.core.common.overlays.menu

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import com.galixo.autoClicker.core.common.base.addDumpTabulationLvl
import com.galixo.autoClicker.core.common.base.extensions.WindowManagerCompat
import com.galixo.autoClicker.core.common.base.extensions.doWhenMeasured
import com.galixo.autoClicker.core.common.base.extensions.safeAddView
import com.galixo.autoClicker.core.common.overlays.R
import com.galixo.autoClicker.core.common.overlays.base.BaseOverlay
import com.galixo.autoClicker.core.common.overlays.di.OverlaysEntryPoint
import com.galixo.autoClicker.core.common.overlays.menu.common.OverlayMenuAnimations
import com.galixo.autoClicker.core.common.overlays.menu.common.OverlayMenuMoveTouchEventHandler
import com.galixo.autoClicker.core.common.overlays.menu.common.OverlayMenuPositionDataSource
import com.galixo.autoClicker.core.common.overlays.menu.common.OverlayMenuResizeController
import dagger.hilt.EntryPoints
import java.io.PrintWriter


abstract class OverlayMenu(
    @StyleRes theme: Int? = null,
) : BaseOverlay(theme = theme, recreateOnRotation = false) {

    val baseLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManagerCompat.TYPE_COMPAT_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSPARENT
    )
    private val menuLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams().apply { copyFrom(baseLayoutParams) }

    val overlayViewLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        copyFrom(baseLayoutParams).apply {
            width = 100
            height = 100
        }
    }

    private val animations: OverlayMenuAnimations = OverlayMenuAnimations()

    internal var resumeOnceShown: Boolean = false
        private set

    internal var destroyOnceHidden: Boolean = false
        private set

    /** The Android window manager. Used to add/remove the overlay menu. */
    lateinit var windowManager: WindowManager

    /** The root view of the menu overlay. Retrieved from [onCreateMenu] implementation. */
    private lateinit var menuLayout: ViewGroup

    /** The view displaying the background of the overlay. */
    private lateinit var menuBackground: ViewGroup

    /** The view containing the buttons as direct children. */
    private lateinit var buttonsContainer: ViewGroup

    /** Handles the window size computing when animating a resize of the overlay. */
    private lateinit var resizeController: OverlayMenuResizeController

    /** Handles the touch events on the move button. */
    private lateinit var moveTouchEventHandler: OverlayMenuMoveTouchEventHandler

    /** Handles the save/load of the position of the menus. */
    private val positionDataSource: OverlayMenuPositionDataSource by lazy {
        EntryPoints.get(context.applicationContext, OverlaysEntryPoint::class.java)
            .overlayMenuPositionDataSource()
    }

    /** Value of the alpha for a disabled item view in the menu. */
    private var disabledItemAlpha: Float = 1f


    private val onLockedPositionChangedListener: (Point?) -> Unit = ::onLockedPositionChanged

    /**
     * Creates the root view of the menu overlay.
     *
     * @param layoutInflater the Android layout inflater.
     *
     * @return the menu root view. It MUST contain a view group within a depth of 2 that contains all menu items in
     *         order for move and hide to work as expected.
     */
    protected abstract fun onCreateMenu(layoutInflater: LayoutInflater): ViewGroup

    @CallSuper
    @SuppressLint("ResourceType")
    override fun onCreate() {
        windowManager = context.getSystemService(WindowManager::class.java)!!
        disabledItemAlpha = context.resources.getFraction(R.dimen.alpha_menu_item_disabled, 1, 1)

        menuLayout = onCreateMenu(context.getSystemService(LayoutInflater::class.java))

        // Set the click listeners on the menu items
        menuBackground = menuLayout.findViewById(R.id.menu_background)
        buttonsContainer = menuLayout.findViewById(R.id.menu_items)
        setupButtons(buttonsContainer)

        // Setup the touch event handler for the move button
        moveTouchEventHandler = OverlayMenuMoveTouchEventHandler(::updateMenuPosition)

        // Restore the last menu position, if any
        menuLayoutParams.gravity = Gravity.TOP or Gravity.START
        positionDataSource.addOnLockedPositionChangedListener(onLockedPositionChangedListener)
        loadMenuPosition(displayConfigManager.displayConfig.orientation)

        // Handle window resize animations
        resizeController = OverlayMenuResizeController(
            backgroundViewGroup = menuBackground,
            resizedContainer = buttonsContainer,
            maximumSize = getWindowMaximumSize(menuBackground),
            windowResizer = ::onNewWindowSize,
        )

        menuBackground.visibility = View.VISIBLE
        if (!windowManager.safeAddView(menuLayout, menuLayoutParams)) {
           // finish()
            return
        }
    }

    private fun setupButtons(buttonsContainer: ViewGroup) {
        buttonsContainer.forEach { view ->
            view.setDebouncedOnClickListener { v ->
                if (resizeController.isAnimating) return@setDebouncedOnClickListener
                onMenuItemClicked(v.id)
            }
        }
    }

    final override fun start() {
        if (lifecycle.currentState != Lifecycle.State.CREATED) return
        if (animations.showAnimationIsRunning) return

        super.start()
        loadMenuPosition(displayConfigManager.displayConfig.orientation)

        Log.d(TAG, "Start show overlay ${hashCode()} animation...")

        menuBackground.visibility = View.VISIBLE
        animations.startShowAnimation(menuBackground, null) {
            Log.d(TAG, "Show overlay ${hashCode()} animation ended")

            if (resumeOnceShown) {
                resumeOnceShown = false
                resume()
            }
        }
    }

    final override fun resume() {
        if (lifecycle.currentState == Lifecycle.State.CREATED) start()
        if (lifecycle.currentState != Lifecycle.State.STARTED) return

        if (animations.showAnimationIsRunning) {
            Log.d(TAG, "Show overlay ${hashCode()} animation is running, delaying resume...")
            resumeOnceShown = true
            return
        }

        forceWindowResize()

        super.resume()
    }

    final override fun stop() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        if (animations.hideAnimationIsRunning) return
        if (lifecycle.currentState == Lifecycle.State.RESUMED) pause()

        saveMenuPosition(displayConfigManager.displayConfig.orientation)

        // Start the hide animation for the menu
        Log.d(TAG, "Start overlay ${hashCode()} hide animation...")
        animations.startHideAnimation(menuBackground, null) {
            Log.d(TAG, "Hide overlay ${hashCode()} animation ended")

            menuBackground.visibility = View.GONE

            super.stop()

            if (destroyOnceHidden) {
                destroyOnceHidden = false
                destroy()
            }
        }
    }

    final override fun destroy() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) return
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) stop()

        if (animations.hideAnimationIsRunning) {
            Log.d(TAG, "Hide overlay ${hashCode()} animation is running, delaying destroy...")
            destroyOnceHidden = true
            return
        }

        // Save last user position
        positionDataSource.removeOnLockedPositionChangedListener(onLockedPositionChangedListener)
        saveMenuPosition(displayConfigManager.displayConfig.orientation)

        windowManager.removeView(menuLayout)

        resizeController.release()
        super@OverlayMenu.destroy()
    }

    /**
     * Handles the screen orientation changes.
     * It will save the menu position for the previous orientation and load and apply the correct position for the new
     * orientation.
     */
    override fun onOrientationChanged() {
        saveMenuPosition(
            if (displayConfigManager.displayConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                Configuration.ORIENTATION_PORTRAIT
            else
                Configuration.ORIENTATION_LANDSCAPE
        )
        loadMenuPosition(displayConfigManager.displayConfig.orientation)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            windowManager.updateViewLayout(menuLayout, menuLayoutParams)
        }
    }

    /**
     * Called when an item (other than move/hide) in the menu has been pressed.
     * @param viewId the pressed view identifier.
     */
    protected open fun onMenuItemClicked(@IdRes viewId: Int): Unit = Unit

    /**
     * Get the maximum size the window can take.
     * @param backgroundView the background view.
     */
    protected open fun getWindowMaximumSize(backgroundView: ViewGroup): Size {
        backgroundView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        return Size(backgroundView.measuredWidth, backgroundView.measuredHeight)
    }

    /**
     * Set the enabled state of a menu item.
     *
     * @param view the view of the menu item to change the state of.
     * @param enabled true to enable the view, false to disable it.
     * @param clickable true to keep the view clickable, false to ignore all clicks on the view. False by default.
     */
    protected fun setMenuItemViewEnabled(view: View, enabled: Boolean, clickable: Boolean = false) {
        view.apply {
            isEnabled = enabled || clickable
            alpha = if (enabled) 1.0f else disabledItemAlpha
        }
    }

    /**
     * Set the visibility of a menu item.
     *
     * @param view the view of the menu item to change the visibility of.
     * @param visible true for visible, false for gone.
     */
    protected fun setMenuItemVisibility(view: View, visible: Boolean) {
        Log.d(TAG, "setMenuItemVisibility for ${hashCode()}, $view to $visible")
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Animates the provided layout changes.
     * Allows using the XML property `animateLayoutChanges`. All changes triggering a window resize should be made using
     * this method.
     *
     * @param layoutChanges the changes triggering a resize.
     */
    protected fun animateLayoutChanges(layoutChanges: () -> Unit) {
        resizeController.animateLayoutChanges(layoutChanges)
    }

    private fun forceWindowResize() {
        Log.d(TAG, "Force window resize")
        //  onNewWindowSize(resizeController.measureMenuSize())
    }

    private fun onNewWindowSize(size: Size) {
        menuLayoutParams.width = size.width
        menuLayoutParams.height = size.height

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "Updating menu window size: ${size.width}/${size.height}")
            windowManager.updateViewLayout(menuLayout, menuLayoutParams)
        }
    }

    /**
     * Called when the user touches the [R.id.btn_move] menu item.
     * Handles the long press and move on this button to drag and drop the overlay menu on the screen.
     *
     * @param event the touch event occurring on the menu item.
     *
     * @return true if the event is handled, false if not.
     */
    fun onMoveTouched(event: MotionEvent): Boolean {
        if (resizeController.isAnimating) return false

        return moveTouchEventHandler.onTouchEvent(menuLayout, event)
    }

    /** Safely sets the position of the overlay menu ensuring it will not be displayed outside the screen. */
    private fun updateMenuPosition(position: Point) {
        menuLayoutParams.x = position.x.coerceIn(0, displayConfigManager.displayConfig.sizePx.x - menuLayout.width)
        menuLayoutParams.y = position.y.coerceIn(0, displayConfigManager.displayConfig.sizePx.y - menuLayout.height)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            Log.d(TAG, "Updating menu window position: ${menuLayoutParams.x}/${menuLayoutParams.y}")
            windowManager.updateViewLayout(menuLayout, menuLayoutParams)
        }
    }

    private fun loadMenuPosition(orientation: Int) {
        val savedPosition = positionDataSource.loadMenuPosition(orientation)
        if (savedPosition != null && savedPosition.x != 0 && savedPosition.y != 0) {
            updateMenuPosition(savedPosition)
        } else {
            menuLayout.doWhenMeasured {
                updateMenuPosition(
                    Point(
                        (displayConfigManager.displayConfig.sizePx.x - menuLayout.width) / 2,
                        (displayConfigManager.displayConfig.sizePx.y / 2) - menuLayout.height,
                    )
                )
            }
        }
    }

    private fun saveMenuPosition(orientation: Int) {
        positionDataSource.saveMenuPosition(
            position = Point(menuLayoutParams.x, menuLayoutParams.y),
            orientation = orientation,
        )
    }

    private fun onLockedPositionChanged(lockedPosition: Point?) {
        if (lockedPosition != null) {
            Log.d(TAG, "Locking menu position of overlay ${hashCode()}")
            saveMenuPosition(displayConfigManager.displayConfig.orientation)
            updateMenuPosition(lockedPosition)
        } else {
            Log.d(TAG, "Unlocking menu position of overlay ${hashCode()}")
            loadMenuPosition(displayConfigManager.displayConfig.orientation)
        }
    }

    override fun dump(writer: PrintWriter, prefix: CharSequence) {
        super.dump(writer, prefix)
        val contentPrefix = prefix.addDumpTabulationLvl()

        writer.apply {
            append(contentPrefix)
                .append("resumeOnceShown=$resumeOnceShown; ")
                .append("destroyOnceHidden=$destroyOnceHidden; ")
                .println()

            animations.dump(writer, contentPrefix)
            positionDataSource.dump(writer, contentPrefix)
        }
    }
}

/** Tag for logs */
private const val TAG = "OverlayMenu"
