package com.galixo.autoClicker.feature.config.ui.actions.save

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.galixo.autoClicker.core.common.overlays.dialog.bottomSheet.OverlayDialogSheet
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.DialogSaveBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class SaveDialog(
    private val onDiscard: () -> Unit,
    private val onSave: () -> Unit,
) : OverlayDialogSheet(R.style.AppTheme) {


    /** ViewBinding containing the views for this dialog. */
    private lateinit var viewBinding: DialogSaveBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogSaveBinding.inflate(LayoutInflater.from(context)).apply {

            actionButtons.apply {
                actionCancel.setText(R.string.discard)
                actionDone.setText(R.string.save)

                actionCancel.setDebouncedOnClickListener { onDiscardButtonClicked() }
                actionDone.setDebouncedOnClickListener { onSaveButtonClicked() }
            }
        }

        return viewBinding.root
    }


    override fun onDialogCreated(dialog: BottomSheetDialog) {}


    private fun onDiscardButtonClicked() {
        back()
        onDiscard.invoke()
    }

    private fun onSaveButtonClicked() {
        onSave.invoke()
        back()
    }

}