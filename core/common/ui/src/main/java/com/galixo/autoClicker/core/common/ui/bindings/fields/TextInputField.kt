package com.galixo.autoClicker.core.common.ui.bindings.fields

import android.text.Editable
import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import com.galixo.autoClicker.core.common.ui.R
import com.galixo.autoClicker.core.common.ui.databinding.IncludeTextInputFieldBinding
import com.galixo.autoClicker.core.common.ui.utils.OnAfterTextChangedListener

fun IncludeTextInputFieldBinding.setLabel(@StringRes labelResId: Int) {
    root.setHint(labelResId)
}

fun IncludeTextInputFieldBinding.setText(text: String?, type: Int = InputType.TYPE_CLASS_TEXT) {
    textField.apply {
        inputType = type
        imeOptions = EditorInfo.IME_ACTION_DONE
        setText(text)
    }
}

fun IncludeTextInputFieldBinding.setError(isError: Boolean) {
    setError(R.string.input_field_error_required, isError)
}

fun IncludeTextInputFieldBinding.setError(@StringRes messageId: Int, isError: Boolean) {
    root.error = if (isError) root.context.getString(messageId) else null
}

fun IncludeTextInputFieldBinding.setOnTextChangedListener(listener: (Editable) -> Unit) {
    textField.addTextChangedListener(OnAfterTextChangedListener(listener))
}