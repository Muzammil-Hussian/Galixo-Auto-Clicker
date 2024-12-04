package com.galixo.autoClicker.modules.settings.dialog.repeatMode

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.ui.bindings.fields.setHelperText
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.databinding.DialogNeverStopBinding
import com.galixo.autoClicker.modules.settings.dialog.repeatMode.enum.RepeatMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepeatModeDialog(private val callback: (mode: RepeatMode, duration : Long, repeats: Int) -> Unit) : DialogFragment() {

    internal companion object {
        internal const val REPEAT_MODE_DIALOG_SETTINGS = "repeat_mode_dialog_settings"
    }

    private val viewModel by viewModels<RepeatModeViewModel>()

    private lateinit var viewBinding: DialogNeverStopBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    updateRadioButton(it.selectedMode)

                    viewBinding.apply {
                        hours.setText(it.hours.toString())
                        minutes.setText(it.minutes.toString())
                        seconds.setText(it.seconds.toString())
                        repeatCycles.setText(it.repeats.toString())
                    }
                }
            }
        }
    }

    private fun updateRadioButton(selectedMode: RepeatMode) {
        viewBinding.neverStopRadioButton.isChecked = selectedMode == RepeatMode.NEVER_STOP
        viewBinding.stopAfterDurationRadioButton.isChecked = selectedMode == RepeatMode.STOP_AFTER_DURATION
        viewBinding.stopAfterRepeatsRadioButton.isChecked = selectedMode == RepeatMode.STOP_AFTER_REPEATS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogNeverStopBinding.inflate(layoutInflater).apply {
            hours.setHelperText("Hours")
            minutes.setHelperText("Minutes")
            seconds.setHelperText("Seconds")

            neverStop.setOnClickListener {
                viewModel.updateRepeatMode(RepeatMode.NEVER_STOP)
            }

            stopAfterDurationMax.setOnClickListener {
                viewModel.updateRepeatMode(RepeatMode.STOP_AFTER_DURATION)
            }

            stopAfterRepeats.setOnClickListener {
                viewModel.updateRepeatMode(RepeatMode.STOP_AFTER_REPEATS)
            }

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

            // Set up listener for repeat input
            repeatCycles.setOnTextChangedListener { text ->
                val repeats = text.toString().toIntOrNull() ?: 10
                viewModel.updateRepeats(repeats)
            }

            // Action buttons
            actionButtons.actionCancel.setOnClickListener {
                dismiss()
            }

            actionButtons.actionDone.setOnClickListener {
                val state = viewModel.state.value
                val duration = (state.hours * 3600 + state.minutes * 60 + state.seconds).toLong()
                callback.invoke(state.selectedMode, duration, state.repeats)
                dismiss()
            }
        }

        return MaterialAlertDialogBuilder(requireContext()).setView(viewBinding.root).create()
    }

}