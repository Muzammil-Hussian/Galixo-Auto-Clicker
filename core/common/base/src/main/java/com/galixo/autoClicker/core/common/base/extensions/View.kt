package com.galixo.autoClicker.core.common.base.extensions

import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.doOnLayout


fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}


fun View.doWhenMeasured(closure: () -> Unit) {
    if (width != 0 && height != 0) {
        closure()
        return
    }

    doOnLayout { doWhenMeasured(closure) }
}

fun View.delayDrawUntil(timeOutMs: Long = DEFAULT_DRAW_DELAY_TIMEOUT_MS, closure: () -> Boolean) {
    val timeOutTs = System.currentTimeMillis() + timeOutMs

    viewTreeObserver.addOnPreDrawListener(
        object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (closure() || timeOutTs < System.currentTimeMillis()) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    false
                }
            }
        }
    )
}

private const val DEFAULT_DRAW_DELAY_TIMEOUT_MS = 3_000L