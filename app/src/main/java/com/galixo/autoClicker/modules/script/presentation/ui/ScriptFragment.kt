package com.galixo.autoClicker.modules.script.presentation.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.base.extensions.beVisibleIf
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.FragmentScriptBinding
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.script.presentation.adapter.ScriptAdapter
import com.galixo.autoClicker.modules.script.presentation.adapter.ScriptItemCallback
import com.galixo.autoClicker.modules.script.presentation.ui.dialog.CreateScenarioDialog
import com.galixo.autoClicker.modules.script.presentation.ui.listener.Listener
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioListViewModel
import com.galixo.autoClicker.utils.extensions.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ScriptFragment : BaseFragment<FragmentScriptBinding>(FragmentScriptBinding::inflate) {

    /** Adapter displaying the click scenarios as a list. */
    private lateinit var adapter: ScriptAdapter

    private val scenarioListViewModel: ScenarioListViewModel by viewModels()


    /** The current dialog being displayed. Null if not displayed. */
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ScriptAdapter(object : ScriptItemCallback {
            override fun onItemMenuClick(menuItem: MenuItem, item: Scenario) {
                when (menuItem.itemId) {
                    R.id.rename -> onRenameClicked(item = item)
                    R.id.duplicate -> onDuplicateScenario(item)
                    R.id.export -> showBackupDialog(false, listOf(item.id.databaseId))
                    R.id.delete -> onDeleteClicked(item = item)
                }
            }

            override fun onStartScenario(item: Scenario) {
                (requireActivity() as? Listener)?.startScenario(item)
            }
        })
    }

    private fun onDuplicateScenario(item: Scenario) {
        scenarioListViewModel.onDuplicationScenario(item)
    }

    override fun onViewCreated() {

        binding.apply {
            list.adapter = this@ScriptFragment.adapter

            addScript.setOnClickListener { onCreateScenarioClicked() }

            importScriptEmpty.setOnClickListener { showBackupDialog(true) }
            importScript.setOnClickListener { showBackupDialog(true) }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { scenarioListViewModel.allScenarios.collect(collector = ::updateScenarioList) }
            }
        }
    }

    private fun onCreateScenarioClicked() {
        CreateScenarioDialog().show(requireActivity().supportFragmentManager, CreateScenarioDialog.TAG)
    }


    /**
     * Observer upon the list of click scenarios.
     * Will update the list/empty view according to the current click scenarios
     */
    private fun updateScenarioList(listContent: List<Scenario>) {

        val isEmpty = listContent.isEmpty()

        binding.apply {
            list.beGoneIf(isEmpty)
            importScript.beGoneIf(isEmpty)
            layoutEmpty.beVisibleIf(isEmpty)
        }
        adapter.submitList(listContent)
    }

    /**
     * Show an AlertDialog from this fragment.
     * This method will ensure that only one dialog is shown at the same time.
     *
     * @param newDialog the new dialog to be shown.
     */
    private fun showDialog(newDialog: AlertDialog) {
        dialog.let {
            Timber.w("Requesting show dialog while another one is one screen.")
            it?.dismiss()
        }

        dialog = newDialog
        newDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        newDialog.setOnDismissListener { dialog = null }
        newDialog.show()
    }

    /**
     * Called when the user clicks on the rename button of a scenario.
     * Create and show the [dialog]. upon Ok press, rename the scenario.
     *
     * @param item the scenario to rename.
     * */
    private fun onRenameClicked(item: Scenario) {

        val inputLayout = TextInputLayout(requireContext()).apply {
            hint = getString(R.string.hint_rename_scenario)
        }


        val inputEditText = TextInputEditText(requireContext()).apply {
            setText(item.name)
            inputLayout.addView(this)
        }

        // Create the MaterialAlertDialog
        showDialog(
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.dialog_title_rename_scenario)
                .setView(inputLayout) // Set the TextInputLayout as the view for the dialog
                .setPositiveButton(R.string.rename) { _, _ ->
                    val newName = inputEditText.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        // Call ViewModel to handle renaming
                        scenarioListViewModel.renameScenario(item, newName)
                    } else {
                        // Optionally show a toast or error if input is empty
                        requireContext().showToast(getString(R.string.error_empty_scenario_name))
                    }
                }.setNegativeButton(android.R.string.cancel, null) // Cancel button dismisses the dialog
                .create()
        )
    }


    private fun showBackupDialog(
        isImport: Boolean,
        ScenariosToBackup: Collection<Long>? = null,
    ) {
        activity?.let {
    //        BackupDialogFragment.newInstance(isImport, ScenariosToBackup).show(it.supportFragmentManager, FRAGMENT_TAG_BACKUP_DIALOG)
        }
    }

    /**
     * Called when the user clicks on the delete button of a scenario.
     * Create and show the [dialog]. Upon Ok press, delete the scenario.
     *
     * @param item the scenario to delete.
     */
    private fun onDeleteClicked(item: Scenario) {
        showDialog(
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.dialog_title_delete_scenario)
                .setMessage(resources.getString(R.string.message_delete_scenario, item.name))
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> scenarioListViewModel.deleteScenario(item) }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        )
    }
}