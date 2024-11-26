package com.galixo.autoClicker.core.common.overlays.dialog

import android.app.Dialog
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import com.galixo.autoClicker.core.common.base.addDumpTabulationLvl
import com.galixo.autoClicker.core.common.base.extensions.WindowManagerCompat
import com.galixo.autoClicker.core.common.overlays.R
import com.galixo.autoClicker.core.common.overlays.base.BaseOverlay
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.PrintWriter

/**
 * Controller for a dialog opened from a service as an overlay.
 *
 * This class ensures that all dialogs opened from a service will have the same behavior. It provides basic lifecycle
 * alike methods to ease the view initialization/cleaning.
 */
abstract class OverlayDialog(@StyleRes theme: Int? = null) :
    BaseOverlay(theme, recreateOnRotation = true) {

    /** The Android InputMethodManager, for ensuring the keyboard dismiss on dialog dismiss. */
    private lateinit var inputMethodManager: InputMethodManager

    /** Touch listener hiding the software keyboard and propagating the touch event normally. */
    private val hideSoftInputTouchListener = { view: View, _: MotionEvent ->
        hideSoftInput()
        view.performClick()
    }

    /** Tells if the dialog is visible. */
    private var isShown = false

    /**
     * The dialog currently displayed by this controller.
     * Null until [onDialogCreated] is called, or if it has been dismissed.
     */
    protected var dialog: Dialog? = null
        private set

    /**
     * Creates the dialog shown by this controller.
     *
     * @return the builder for the dialog to be created.
     */
    protected abstract fun onCreateView(): ViewGroup

    /**
     * Setup the dialog view.
     * Called once the dialog is created and first shown, it allows the implementation to initialize the content views.
     *
     * @param dialog the newly created dialog.
     */
    protected abstract fun onDialogCreated(dialog: Dialog)

    final override fun onCreate() {
        inputMethodManager = context.getSystemService(InputMethodManager::class.java)

        val view = onCreateView()

        // Create a Material Alert Dialog with rounded corners
        dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_Rounded)
            .setView(view)
            .setCancelable(false)
            .setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    this@OverlayDialog.back()
                    true
                } else {
                    false
                }
            }
            .create()

        // Set additional properties for the dialog's window
        dialog?.window?.apply {
            setType(WindowManagerCompat.TYPE_COMPAT_OVERLAY)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            decorView.setOnTouchListener(hideSoftInputTouchListener)
        }

        onDialogCreated(dialog!!)
    }

    @CallSuper
    override fun onStart() {
        if (isShown) return

        isShown = true
        dialog?.show()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    @CallSuper
    override fun onStop() {
        if (!isShown) return

        hideSoftInput()
        dialog?.hide()
        isShown = false
    }

    @CallSuper
    override fun onDestroy() {
        dialog?.dismiss()
        dialog = null
    }

    /** Hide automatically the software keyboard when the provided view loses focus. */
    fun hideSoftInputOnFocusLoss(view: View) {
        view.setOnFocusChangeListener { v, hasFocus ->
            if (view.id == v.id && !hasFocus) {
                hideSoftInput()
            }
        }
    }

    /** Hide the software keyboard. */
    private fun hideSoftInput() {
        dialog?.let {
            inputMethodManager.hideSoftInputFromWindow(it.window!!.decorView.windowToken, 0)
        }
    }

    override fun dump(writer: PrintWriter, prefix: CharSequence) {
        super.dump(writer, prefix)
        val contentPrefix = prefix.addDumpTabulationLvl()

        writer.append(contentPrefix)
            .append("isDialogShown=$isShown; ")
            .println()
    }
}