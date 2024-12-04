package com.galixo.autoClicker.feature.config.ui.actions.repeatMode

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.core.common.ui.databinding.DialogNeverStopBinding
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ButtonAction
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ScenarioConfigViewModel
import kotlinx.coroutines.launch


class RepeatModeDialog(
    private val onConfigDiscarded: () -> Unit,
    private val onConfigSaved: () -> Unit,
) : OverlayDialog(R.style.AppTheme) {

    private val viewModel: ScenarioConfigViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioConfigViewModel() })

    private lateinit var viewBinding: DialogNeverStopBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogNeverStopBinding.inflate(LayoutInflater.from(context)).apply {
            with(actionButtons) {
                actionCancel.setDebouncedOnClickListener { handleButtonAction(ButtonAction.CANCEL) }
                actionDone.setDebouncedOnClickListener { handleButtonAction(ButtonAction.DONE) }
            }
        }
        return viewBinding.root
    }

    override fun onDialogCreated(dialog: AlertDialog) {
        Log.i(TAG, "onDialogCreated: $viewModel")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { }
            }
        }
    }


    private fun handleButtonAction(action: ButtonAction) {
        when (action) {
            ButtonAction.CANCEL -> {
                onConfigDiscarded.invoke()
                back()
            }

            ButtonAction.DONE -> {
                onConfigSaved.invoke()
                back()
            }
        }
    }
}

private const val TAG = "RepeatModeDialog"