package com.galixo.autoClicker.feature.config.ui.actions.save

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.DialogSaveBinding


class SaveDialog(
    private val onDiscard: () -> Unit,
    private val onSave: () -> Unit,
) : OverlayDialog(R.style.AppTheme) {


    /** ViewBinding containing the views for this dialog. */
    private lateinit var viewBinding: DialogSaveBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogSaveBinding.inflate(LayoutInflater.from(context)).apply {


            discard.setDebouncedOnClickListener { onDiscardButtonClicked() }

            save.setDebouncedOnClickListener { onSaveButtonClicked() }
        }

        return viewBinding.root
    }


    override fun onDialogCreated(dialog: Dialog) {
    }


    private fun onDiscardButtonClicked() {
        onDiscard.invoke()
        back()
    }

    private fun onSaveButtonClicked() {
        onSave.invoke()
        back()
    }

}