package com.galixo.autoClicker.modules.dialog.base

import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.galixo.autoClicker.R

abstract class BaseDialog(private val isFullScreen: Boolean = false) : DialogFragment() {

    override fun onStart() {
        super.onStart()

        if (isFullScreen) {

            dialog?.window?.apply {
                val width =
                    resources.displayMetrics.widthPixels - resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
                setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.CENTER)
                setDimAmount(0.75f)
                attributes?.horizontalMargin =
                    resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
                        .toFloat() / width.toFloat()
                attributes?.verticalMargin =
                    resources.getDimensionPixelSize(R.dimen.dialog_vertical_margin)
                        .toFloat() / resources.displayMetrics.heightPixels.toFloat()
            }
        }
    }
}