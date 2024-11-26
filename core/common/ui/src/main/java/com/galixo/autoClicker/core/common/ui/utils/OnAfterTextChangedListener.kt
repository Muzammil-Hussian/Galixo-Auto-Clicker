package com.galixo.autoClicker.core.common.ui.utils

import android.text.Editable
import android.text.TextWatcher

/** [TextWatcher] implementation allowing to only declare [TextWatcher.afterTextChanged] in implementation. */
class OnAfterTextChangedListener(private val callback: (Editable) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable?) {
        s?.let { callback(it) }
    }
}