package com.galixo.autoClicker.modules.permissions.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.galixo.autoClicker.databinding.DialogWatchTutorialBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class WatchTutorialDialog : DialogFragment() {

    internal companion object {
        internal const val WATCH_TUTORIAL_DIALOG_TAG = "WatchTutorialDialog"
    }

    private lateinit var viewBinding: DialogWatchTutorialBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = DialogWatchTutorialBinding.inflate(layoutInflater).apply {
            cancel.setOnClickListener {
                Timber.i("Cancel button clicked")
                dismiss()
            }
            grantPermission.setOnClickListener {
                Timber.i("Grant Permission button clicked")
                dismiss()
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(viewBinding.root)
            .setCancelable(true)
            .create()

        dialog.setOnShowListener {
            Timber.i("dialog is shown")
        }

        return dialog
    }


}