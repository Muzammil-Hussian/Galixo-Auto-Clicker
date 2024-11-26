package com.galixo.autoClicker.utils.extensions

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.galixo.autoClicker.R
import com.google.android.material.card.MaterialCardView


fun MaterialCardView.updateStrokeColor(isSelected: Boolean) {
    changeStrokeColor(if (isSelected) R.color.primaryColor else R.color.transparent)
}

fun MaterialCardView.changeStrokeColor(color: Int) {
    setStrokeColor(
        ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                color
            )
        )
    )
}