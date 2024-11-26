package com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig

import android.app.Dialog
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.data.getConfigPreferences
import com.galixo.autoClicker.feature.config.data.putClickRepeatDelayConfig
import com.galixo.autoClicker.feature.config.data.putSwipeDurationConfig
import com.galixo.autoClicker.feature.config.data.putSwipeRepeatDelayConfig
import com.galixo.autoClicker.feature.config.databinding.DialogScenarioConfigBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.domain.getDefaultClickRepeatDelay
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeDurationMs
import kotlinx.coroutines.launch

class ScenarioConfigDialog(
    private val onConfigSaved: () -> Unit,
    private val onConfigDiscarded: () -> Unit,
) : OverlayDialog(R.style.AppTheme) {

    /** View model for the container dialog. */
    private val dialogViewModel: ScenarioConfigViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioConfigViewModel() },
    )

    private lateinit var viewBinding: DialogScenarioConfigBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogScenarioConfigBinding.inflate(LayoutInflater.from(context)).apply {

            setupActionButtons()
            with(interval) {
                label.setText(R.string.interval_label)
                fieldText.apply {
                    setText(context.getDefaultClickRepeatDelay().toString())
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                }
            }

            with(swipeDuration) {
                label.setText(R.string.swipe_duration)
                fieldText.apply {
                    setText(context.getDefaultSwipeDurationMs().toString())
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                }
            }

            stopAfter.textField.setText(context.resources.getString(R.string.never_stop))

            toggleSwitch.setOnClickListener {
                dialogViewModel.toggleRandomization()
            }
        }
        return viewBinding.root
    }

    override fun onDialogCreated(dialog: Dialog) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { dialogViewModel.canBeSaved.collect(::updateSaveButtonState) }
                launch { dialogViewModel.repeatInfiniteState.collect(true::equals) }
                launch { dialogViewModel.randomization.collect(::updateFieldRandomization) }
            }
        }
    }

    private fun updateFieldRandomization(randomize: Boolean) {
        viewBinding.toggleSwitch.isChecked = randomize
    }

    private fun updateSaveButtonState(isEnabled: Boolean) {
        viewBinding.actionButtons.actionDone.isEnabled = isEnabled
    }

    private fun DialogScenarioConfigBinding.setupActionButtons() {
        with(actionButtons) {
            actionCancel.apply {
                setText(R.string.cancel)
                setDebouncedOnClickListener { handleButtonAction(ButtonAction.CANCEL) }
            }
            actionDone.apply {
                setText(R.string.done)
                setDebouncedOnClickListener { handleButtonAction(ButtonAction.DONE) }
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
                Log.i(TAG, "handleButtonAction: interval: ${viewBinding.interval.fieldText.textField.text}")
                Log.i(TAG, "handleButtonAction: swipeDuration: ${viewBinding.swipeDuration.fieldText.textField.text}")
                context.getConfigPreferences().edit().putSwipeDurationConfig(viewBinding.swipeDuration.fieldText.textField.text.toString().toLong())
                    .putClickRepeatDelayConfig(viewBinding.interval.fieldText.textField.text.toString().toLong())
                    .putSwipeRepeatDelayConfig(viewBinding.interval.fieldText.textField.text.toString().toLong())
                    .apply()


                back()
            }
        }
    }


    private companion object {
        const val TAG = "ScenarioConfigDialog"
        const val MAX_LENGTH = 6
    }

    private enum class ButtonAction { CANCEL, DONE }
}
