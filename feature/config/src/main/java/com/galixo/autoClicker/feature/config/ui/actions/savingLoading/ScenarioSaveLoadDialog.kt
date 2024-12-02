package com.galixo.autoClicker.feature.config.ui.actions.savingLoading

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialog
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.load.LoadActionContent
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save.SaveActionContent
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch


class ScenarioSaveLoadDialog(
    private val onConfigSaved: () -> Unit,
    private val onConfigDiscarded: () -> Unit,
) : NavBarDialog(R.style.AppTheme) {

    private val viewModel: ScenarioSavingLoadingViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioSavingLoadingViewModel() }
    )

    override fun inflateMenu(navBarView: NavigationBarView) {
        navBarView.inflateMenu(R.menu.menu_scenario_saving_loading)
    }

    override fun onCreateContent(navItemId: Int): NavBarDialogContent = when (navItemId) {
        R.id.page_save -> SaveActionContent(context.applicationContext,viewModel)
        R.id.page_load -> LoadActionContent(context.applicationContext,viewModel)
        else -> throw IllegalArgumentException("Unknown menu id $navItemId")
    }

    override fun onDialogCreated(dialog: AlertDialog) {
        super.onDialogCreated(dialog)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    Log.i(TAG, "onDialogCreated: $viewModel")
                }
            }
        }
    }


    override fun onDialogButtonPressed(buttonType: DialogNavigationButton) {
        Log.i(TAG, "onDialogButtonPressed: $buttonType")
        if (buttonType == DialogNavigationButton.SAVE) {
            onConfigSaved()
            super.back()
            return
        }

        back()
    }

    override fun back() {
        onConfigDiscarded()
        super.back()
    }

    override fun onContentViewChanged(navItemId: Int) {
        super.onContentViewChanged(navItemId)
        Log.i(TAG, "onContentViewChanged: $navItemId")
    }


}

private const val TAG = "ScenarioSaveLoadDialog"