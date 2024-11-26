package com.galixo.autoClicker.core.common.base.extensions

import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import java.lang.reflect.Field

object WindowManagerCompat {

    /** WindowManager LayoutParams type for a window over applications. */
    @Suppress("DEPRECATION")
    @JvmField
    val TYPE_COMPAT_OVERLAY =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE

}

fun WindowManager.safeAddView(view: View?, params: WindowManager.LayoutParams?): Boolean {
    if (view == null || params == null) return false

    return try {
        addView(view, params)
        true
    } catch (ex: WindowManager.BadTokenException) {
        Log.e(TAG, "Can't add view to window manager, permission is denied !")
        false
    }
}

fun WindowManager.LayoutParams.disableMoveAnimations() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        setCanPlayMoveAnimation(false)
    } else {
        val wp = WindowManager.LayoutParams()
        val className = "android.view.WindowManager\$LayoutParams"
        try {
            val layoutParamsClass = Class.forName(className)
            val noAnimFlagField: Field = layoutParamsClass.getField("PRIVATE_FLAG_NO_MOVE_ANIMATION")
            layoutParamsClass.getField("privateFlags").apply {
                setInt(wp, getInt(wp) or noAnimFlagField.getInt(wp))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't disable move animations !")
        }
    }
}

private const val TAG = "WindowManagerExt"