package com.galixo.autoClicker.modules.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.galixo.autoClicker.databinding.RenameScenarioDialogBinding
import com.galixo.autoClicker.modules.dialog.base.RoundedBottomSheetDialogFragment


class RenameScenarioDialog(
    private val callback: (folder: String) -> Unit
) : RoundedBottomSheetDialogFragment<RenameScenarioDialogBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RenameScenarioDialogBinding
        get() = RenameScenarioDialogBinding::inflate

    override fun initViews() {
        super.initViews()

        binding.rename.setOnClickListener {
            callback.invoke(binding.editValueLayout.textField.text.toString())
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        internal const val TAG = "RenameScenarioDialog"


    }
}