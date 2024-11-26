package com.galixo.autoClicker.core.common.ui.bindings.fields

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import com.galixo.autoClicker.core.common.ui.R
import com.galixo.autoClicker.core.common.ui.databinding.IncludeFieldEditTextBinding
import com.galixo.autoClicker.core.common.ui.utils.OnAfterTextChangedListener


fun IncludeFieldEditTextBinding.setLabel(@StringRes labelResId: Int) {
    root.setHint(labelResId)
}

fun IncludeFieldEditTextBinding.setText(text: String?, type: Int = InputType.TYPE_CLASS_NUMBER) {
    textField.apply {
        inputType = type
        imeOptions = EditorInfo.IME_ACTION_DONE
        setText(text)
    }
}

fun IncludeFieldEditTextBinding.setError(isError: Boolean) {
    setError(R.string.input_field_error_required, isError)
}

fun IncludeFieldEditTextBinding.setError(@StringRes messageId: Int, isError: Boolean) {
    root.error = if (isError) root.context.getString(messageId) else null
}

fun IncludeFieldEditTextBinding.onEditorActionListener(value: (String) -> Unit) {
    textField.apply {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearFocus()
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(textField.windowToken, 0)
                value.invoke(text.toString())
                true
            } else {
                false
            }
        }
    }
}

fun IncludeFieldEditTextBinding.setOnTextChangedListener(listener: (Editable) -> Unit) {
    textField.addTextChangedListener(OnAfterTextChangedListener(listener))
}