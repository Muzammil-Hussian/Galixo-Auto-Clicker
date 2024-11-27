package com.galixo.autoClicker.modules.permissions.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.galixo.autoClicker.databinding.DialogWatchTutorialBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WatchTutorialDialog : DialogFragment() {

    internal companion object {
        internal const val WATCH_TUTORIAL_DIALOG_TAG = "WatchTutorialDialog"
    }

    private lateinit var viewBinding: DialogWatchTutorialBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = DialogWatchTutorialBinding.inflate(layoutInflater).apply {
            cancel.setOnClickListener {
                Log.i(TAG, "Cancel button clicked")
                dismiss()
            }
            grantPermission.setOnClickListener {
                Log.i(TAG, "Grant Permission button clicked")
                dismiss()
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(viewBinding.root)
            .setCancelable(true)
            .create()

        dialog.setOnShowListener {
            Log.i(TAG, "dialog is shown")
        }

        return dialog
    }


}

private const val TAG = "WatchTutorialDialog"