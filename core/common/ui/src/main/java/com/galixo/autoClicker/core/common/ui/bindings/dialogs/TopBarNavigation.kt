package com.galixo.autoClicker.core.common.ui.bindings.dialogs

import com.galixo.autoClicker.core.common.ui.databinding.IncludeDialogNavigationBarBinding


fun IncludeDialogNavigationBarBinding.setButtonEnabledState(buttonType: DialogNavigationButton, enabled: Boolean) {
    when (buttonType) {
        DialogNavigationButton.SAVE -> buttonSave.isEnabled = enabled
        DialogNavigationButton.DISMISS -> buttonDismiss.isEnabled = enabled
    }
}

fun IncludeDialogNavigationBarBinding.setButtonVisibility(buttonType: DialogNavigationButton, visibility: Int) {
    when (buttonType) {
        DialogNavigationButton.SAVE -> buttonSave.visibility = visibility
        DialogNavigationButton.DISMISS -> buttonDismiss.visibility = visibility
    }
}

enum class DialogNavigationButton {
    DISMISS,
    SAVE,
}