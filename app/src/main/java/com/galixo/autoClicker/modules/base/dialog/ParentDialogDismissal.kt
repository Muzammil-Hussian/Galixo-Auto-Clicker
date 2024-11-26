package com.galixo.autoClicker.modules.base.dialog

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment

open class ParentDialogDismissal : DialogFragment() {

    var dismissCallback: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.invoke()
    }
}