package com.galixo.autoClicker.core.common.ui.bindings

import android.view.View
import android.widget.TextView

internal fun TextView.setTextOrGone(textToSet: String?) {
    visibility = if (textToSet == null) View.GONE else View.VISIBLE
    text = textToSet
}