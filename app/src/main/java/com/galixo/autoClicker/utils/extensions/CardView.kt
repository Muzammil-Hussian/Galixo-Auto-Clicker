package com.galixo.autoClicker.utils.extensions

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.galixo.autoClicker.R
import com.google.android.material.card.MaterialCardView


fun MaterialCardView.updateStrokeColor(isSelected: Boolean) {
//    changeColor(if (isSelected) R.color.primaryColor else R.color.border_stroke_color_black_alpha_12)
    changeColor(R.color.border_stroke_color_black_alpha_12)
}

fun MaterialCardView.updateCardBackgroundColor(isSelected: Boolean) {
    setCardBackgroundColor(ContextCompat.getColor(context, if (isSelected) R.color.primaryLight else R.color.transparent))
}

fun MaterialCardView.changeColor(color: Int) {
    setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, color)))
}

