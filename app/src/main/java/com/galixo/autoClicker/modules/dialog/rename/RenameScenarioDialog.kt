package com.galixo.autoClicker.modules.dialog.rename

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.ui.bindings.fields.setError
import com.galixo.autoClicker.core.common.ui.bindings.fields.setLabel
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.bindings.others.setDescription
import com.galixo.autoClicker.core.common.ui.bindings.others.setTitle
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.DialogRenameScenarioBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RenameScenarioDialog(
    private val scenario: Scenario
) : DialogFragment() {
    private val viewModel: RenameScenarioDialogViewModel by viewModels()
    private lateinit var viewBinding: DialogRenameScenarioBinding

    companion object {
        internal const val TAG = "RenameScenarioDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.isValidToRename.collect(::updateRenameAction) }
                launch { viewModel.name.collect(viewBinding.renameField::setText) }
                launch { viewModel.nameError.collect(viewBinding.renameField::setError) }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel.setRenameScenario(scenario)
        viewBinding = DialogRenameScenarioBinding.inflate(layoutInflater).apply {

            titleAndDescription.apply {
                setTitle(getString(R.string.dialog_title_rename_scenario))
                setDescription(getString(R.string.script_name))
            }

            renameField.apply {
                setLabel(R.string.input_field_label_name)
                setOnTextChangedListener { viewModel.setNewName(it.toString()) }
                textField.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(requireContext().resources.getInteger(R.integer.name_max_length))
                )
            }

            cancel.setOnClickListener { dismiss() }
            rename.setOnClickListener {
                viewModel.renameScenario()
                dismiss()
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(viewBinding.root)
            .create()
    }

/*    override fun onStart() {
        super.onStart()
        viewBinding.renameField.textField.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }*/

    private fun updateRenameAction(isValid: Boolean) {
        viewBinding.rename.isEnabled = isValid
    }

}