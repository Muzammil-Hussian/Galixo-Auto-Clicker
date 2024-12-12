package com.galixo.autoClicker.feature.config.ui.actions.repeatMode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.bottomSheet.OverlayDialogSheet
import com.galixo.autoClicker.core.common.ui.bindings.fields.setHelperText
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.databinding.DialogNeverStopBinding
import com.galixo.autoClicker.core.common.ui.enum.RepeatMode
import com.galixo.autoClicker.core.common.ui.utils.MinMaxInputFilter
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MAX_MS
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MIN_MS
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ButtonAction
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ScenarioConfigViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RepeatModeDialog(
    private val onConfigDiscarded: () -> Unit,
    private val onConfigSaved: () -> Unit,
) : OverlayDialogSheet(R.style.AppTheme) {

    private val viewModel: ScenarioConfigViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioConfigViewModel() })

    private lateinit var viewBinding: DialogNeverStopBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogNeverStopBinding.inflate(LayoutInflater.from(context)).apply {

            hours.setHelperText("Hours")
            minutes.setHelperText("Minutes")
            seconds.setHelperText("Seconds")

            neverStop.setOnClickListener { viewModel.updateRepeatMode(RepeatMode.NEVER_STOP) }

            stopAfterDurationMax.setOnClickListener { viewModel.updateRepeatMode(RepeatMode.STOP_AFTER_DURATION) }

            stopAfterRepeats.setOnClickListener { viewModel.updateRepeatMode(RepeatMode.STOP_AFTER_REPEATS_COUNT) }

            // Set up listeners for time inputs
            hours.setOnTextChangedListener { text ->
                val hours = text.toString().toIntOrNull() ?: 0
                viewModel.updateTimeDuration(hours, viewModel.state.value.minutes, viewModel.state.value.seconds)
            }

            minutes.setOnTextChangedListener { text ->
                val minutes = text.toString().toIntOrNull() ?: 0
                viewModel.updateTimeDuration(viewModel.state.value.hours, minutes, viewModel.state.value.seconds)
            }

            seconds.setOnTextChangedListener { text ->
                val seconds = text.toString().toIntOrNull() ?: 0
                viewModel.updateTimeDuration(viewModel.state.value.hours, viewModel.state.value.minutes, seconds)
            }

            repeatCycles.apply {
                textField.filters = arrayOf(MinMaxInputFilter(REPEAT_DELAY_MIN_MS.toInt(), REPEAT_DELAY_MAX_MS.toInt()))

                setOnTextChangedListener {
                    val enteredValue = it.toString().toIntOrNull() ?: 0
                    viewModel.updateRepeats(enteredValue)
                }
            }

            with(actionButtons) {
                actionCancel.setDebouncedOnClickListener { handleButtonAction(ButtonAction.CANCEL) }
                actionDone.setDebouncedOnClickListener { handleButtonAction(ButtonAction.DONE) }
            }
        }
        return viewBinding.root
    }

    override fun onDialogCreated(dialog: BottomSheetDialog) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    updateRadioButton(it.selectedMode)
                    viewBinding.apply {
                        hours.setText(it.hours.toString())
                        minutes.setText(it.minutes.toString())
                        seconds.setText(it.seconds.toString())
                        repeatCycles.setText(it.repeatCount.toString())
                    }
                }
            }
        }
    }

    private fun updateRadioButton(selectedMode: RepeatMode) {
        viewBinding.neverStopRadioButton.isChecked = selectedMode == RepeatMode.NEVER_STOP
        viewBinding.stopAfterDurationRadioButton.isChecked = selectedMode == RepeatMode.STOP_AFTER_DURATION
        viewBinding.stopAfterRepeatsRadioButton.isChecked = selectedMode == RepeatMode.STOP_AFTER_REPEATS_COUNT
    }

    private fun handleButtonAction(action: ButtonAction) {
        when (action) {
            ButtonAction.CANCEL -> {
                onConfigDiscarded.invoke()
                back()
            }

            ButtonAction.DONE -> {
                val repeatMode = viewModel.state.value.selectedMode
                val hours = viewModel.state.value.hours
                val minutes = viewModel.state.value.minutes
                val seconds = viewModel.state.value.seconds
                val repeats = viewModel.state.value.repeatCount

                val currentScenario = viewModel.editionRepository.editedScenario.value ?: return

                val updatedScenario = currentScenario.copy(
                    isRepeatInfinite = repeatMode == RepeatMode.NEVER_STOP,
                    maxDurationSec = (hours * 3600) + (minutes * 60) + seconds,
                    repeatCount = if (repeats > 1) repeats else 1
                )

                viewModel.updateScenario(updatedScenario)
                onConfigSaved.invoke()
                back()
            }
        }
    }
}