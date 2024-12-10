package com.galixo.autoClicker.feature.config.ui.actions.savingLoading

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.tab.TabDialog
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.load.LoadActionContent
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save.SaveActionContent
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class ScenarioSaveLoadDialog(
    private val onConfigSaved: () -> Unit,
    private val onConfigDiscarded: () -> Unit,
) : TabDialog(R.style.AppTheme) {

    private val viewModel: ScenarioSavingLoadingViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioSavingLoadingViewModel() }
    )

    override fun inflateMenu(tabLayout: TabLayout) {
        Log.i(TAG, "inflateMenu: ")
        tabLayout.apply {
            addTab(newTab().setText(R.string.page_save))
            addTab(newTab().setText(R.string.page_load))
        }
    }

    override fun onCreateContent(tabItemId: Int): NavBarDialogContent {
        Log.i(TAG, "onCreateContent: $tabItemId")
        return when (tabItemId) {
            0 -> SaveActionContent(context.applicationContext)
            1 -> LoadActionContent(context.applicationContext, viewModel = viewModel)
            else -> throw IllegalArgumentException("Unknown tab id $tabItemId")
        }
    }

    override fun onDialogCreated(dialog: AlertDialog) {
        super.onDialogCreated(dialog)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { Log.i(TAG, "onDialogCreated: ") }
            }
        }
    }

    override fun onDialogButtonPressed(buttonType: DialogNavigationButton) {
        Log.i(TAG, "onDialogButtonPressed: ")
        when (buttonType) {
            DialogNavigationButton.SAVE -> {
                onConfigSaved()
                super.back()
                return
            }

            else -> back()
        }
    }

    override fun back() {
        Log.i(TAG, "back: ")
        onConfigDiscarded()
        super.back()
    }

    override fun onContentViewChanged(tabItemId: Int) {
        super.onContentViewChanged(tabItemId)
        Log.i(TAG, "onContentViewChanged: $tabItemId")
    }
}

private const val TAG = "ScenarioSaveLoadDialog"
