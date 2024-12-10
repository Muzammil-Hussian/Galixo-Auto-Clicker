package com.galixo.autoClicker.modules.script.presentation.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.galixo.autClicker.feature.backup.ui.BackupDialogFragment
import com.galixo.autClicker.feature.backup.ui.BackupDialogFragment.Companion.FRAGMENT_TAG_BACKUP_DIALOG
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.base.extensions.beVisibleIf
import com.galixo.autoClicker.core.common.ui.bindings.others.setDescription
import com.galixo.autoClicker.core.common.ui.bindings.others.setTitle
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.FragmentScriptBinding
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.dialog.rename.RenameScenarioDialog
import com.galixo.autoClicker.modules.script.presentation.adapter.ScriptAdapter
import com.galixo.autoClicker.modules.script.presentation.adapter.ScriptItemCallback
import com.galixo.autoClicker.modules.script.presentation.ui.dialog.ScenarioCreationDialog
import com.galixo.autoClicker.modules.script.presentation.ui.dialog.ScriptCreationFABDialog
import com.galixo.autoClicker.modules.script.presentation.ui.listener.Listener
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioListViewModel
import com.galixo.autoClicker.modules.troubleShoot.ui.TroubleShootingActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScriptFragment : BaseFragment<FragmentScriptBinding>(FragmentScriptBinding::inflate) {

    private var dialog: AlertDialog? = null
    private lateinit var adapter: ScriptAdapter
    private val scenarioListViewModel: ScenarioListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
    }

    override fun onViewCreated() {
        setupToolbarMenuItems()
        initViews()
        onClickListener()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { scenarioListViewModel.allScenarios.collect(collector = ::updateScenarioList) }
            }
        }
    }

    private fun initViews() {
        binding.apply {
            titleAndDescription.apply {
                setTitle(R.string.permission_needed)
                setDescription(R.string.permission_needed_desc)
            }
            list.adapter = this@ScriptFragment.adapter
        }
    }

    private fun canShowDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(context)
    }


    private fun onClickListener() {
        binding.apply {
            createScript.setOnClickListener { onCreateScenarioClicked() }
            createScriptFab.setOnClickListener {
                ScriptCreationFABDialog.showDialog(
                    parentFragmentManager,
                    it,
                    onCreateNew = { onCreateScenarioClicked() },
                    onImportScript = { showBackupDialog(true) }
                )
            }
            fixItNow.setOnClickListener {
                startActivity(Intent(requireContext(), TroubleShootingActivity::class.java))
            }
        }
    }

    private fun setupToolbarMenuItems() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_troubleshooting -> {
                        startActivity(Intent(requireContext(), TroubleShootingActivity::class.java))
                        true
                    }

                    R.id.action_settings -> {
                        findNavController().navigate(ScriptFragmentDirections.actionNavigationScriptToNavigationSetting())
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupAdapter() {
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

    private fun onCreateScenarioClicked() {
        ScenarioCreationDialog().show(
            requireActivity().supportFragmentManager,
            ScenarioCreationDialog.TAG
        )
    }


    /**
     * Observer upon the list of click scenarios.
     * Will update the list/empty view according to the current click scenarios
     */
    private fun updateScenarioList(listContent: List<Scenario>) {
        Log.i(TAG, "updateScenarioList: ${listContent.size}")
        val isEmpty = listContent.isEmpty()
        binding.apply {
            list.beGoneIf(isEmpty)
            createScriptFab.beGoneIf(isEmpty)
            layoutEmpty.beVisibleIf(isEmpty)
        }
        adapter.submitList(null)
        adapter.submitList(listContent)
    }

    private fun onRenameClicked(item: Scenario) = RenameScenarioDialog(item).show(requireActivity().supportFragmentManager, RenameScenarioDialog.TAG)

    private fun showBackupDialog(
        isImport: Boolean,
        scenariosToBackup: Collection<Long>? = null,
    ) {
        activity?.let {
            BackupDialogFragment.newInstance(isImport, scenariosToBackup)
                .show(it.supportFragmentManager, FRAGMENT_TAG_BACKUP_DIALOG)
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
                .setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                    scenarioListViewModel.deleteScenario(item)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
        )
    }

    /**
     * Show an AlertDialog from this fragment.
     * This method will ensure that only one dialog is shown at the same time.
     *
     * @param newDialog the new dialog to be shown.
     */
    private fun showDialog(newDialog: AlertDialog) {
        dialog.let {
            Log.w(TAG, "Requesting show dialog while another one is one screen.")
            it?.dismiss()
        }

        dialog = newDialog
        newDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        newDialog.setOnShowListener {

        }
        newDialog.setOnDismissListener { dialog = null }

        newDialog.show()
    }
}

private const val TAG = "ScriptFragment"