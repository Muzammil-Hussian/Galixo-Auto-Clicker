package com.galixo.autoClicker.core.common.ui.bindings.fields

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.galixo.autoClicker.core.common.ui.databinding.IncludeTimeInputBinding
import com.galixo.autoClicker.core.common.ui.utils.OnAfterTextChangedListener


fun IncludeTimeInputBinding.setText(text: String?, type: Int = InputType.TYPE_CLASS_NUMBER) {
    textField.apply {
        inputType = type
        imeOptions = EditorInfo.IME_ACTION_DONE
        setText(text)
        setSelection(text?.length ?: 0)
    }
}

fun IncludeTimeInputBinding.setHelperText(text: String?) {
    helperText.text = text
}


fun IncludeTimeInputBinding.onEditorActionListener(value: (String) -> Unit) {
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

fun IncludeTimeInputBinding.setOnTextChangedListener(listener: (Editable) -> Unit) {
    textField.addTextChangedListener(OnAfterTextChangedListener(listener))
}