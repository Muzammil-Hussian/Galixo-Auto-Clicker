package com.galixo.autoClicker.feature.config.ui.actions.swipe

import android.app.Dialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.GESTURE_DURATION_MAX_VALUE
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setSelectedItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.timeUnitDropdownItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.toDurationMs
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.utils.MinMaxInputFilter
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MAX_MS
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MIN_MS
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.DialogConfigSwipeActionBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SwipePointDialog(
    private val swipeAction: Action.Swipe,
    private val onConfirmClicked: (Action.Swipe) -> Unit,
    private val onDismissClicked: () -> Unit,
) : OverlayDialog(R.style.AppTheme) {

    private val viewModel: SwipePointViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { swipePointViewModel() }
    )

    private lateinit var viewBinding: DialogConfigSwipeActionBinding

    override fun onCreateView(): ViewGroup {
        viewModel.setEditedSwipe(swipeAction)

        viewBinding = DialogConfigSwipeActionBinding.inflate(LayoutInflater.from(context)).apply {

            with(interval) {
                label.setText(R.string.interval_description)

                fieldText.apply {
                    textField.filters = arrayOf(MinMaxInputFilter(REPEAT_DELAY_MIN_MS.toInt(), REPEAT_DELAY_MAX_MS.toInt()))

                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getIntervalUnit()) ?: 0L
                        viewModel.setRepeatDelay(convertedValue)

                        val isValid = validateInput(convertedValue, viewModel.getIntervalUnit())
                        updateHelperText(helperText, isValid, viewModel.getIntervalUnit())
                        updateSaveAction(isValid)
                    }

                    timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                        viewModel.setIntervalUnit(selectedUnit)
                        val isValid = validateInput(viewModel.getRepeatDelayMs(), selectedUnit)
                        updateHelperText(helperText, isValid, selectedUnit)
                        updateSaveAction(isValid)
                    })
                }
            }

            with(swipeDuration) {
                label.setText(R.string.swipe_duration)
                fieldText.apply {
                    textField.filters = arrayOf(MinMaxInputFilter(1, GESTURE_DURATION_MAX_VALUE.toInt()))

                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getSwipeDurationUnit()) ?: 0L
                        viewModel.setPressDurationMs(convertedValue)

                        val isValid = validateInput(convertedValue, viewModel.getSwipeDurationUnit())
                        updateHelperText(helperText, isValid, viewModel.getSwipeDurationUnit())
                        updateSaveAction(isValid)
                    }

                    timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                        viewModel.setSwipeDurationUnit(selectedUnit)
                        val isValid = validateInput(viewModel.getPressDurationMs(), selectedUnit)
                        updateHelperText(helperText, isValid, selectedUnit)
                        updateSaveAction(isValid)
                    })
                }
            }

            with(actionButtons) {
                actionCancel.setDebouncedOnClickListener { onDismissButtonClicked() }
                actionDone.setDebouncedOnClickListener { onSaveButtonClicked() }
            }
        }

        return viewBinding.root
    }

    override fun onDialogCreated(dialog: Dialog) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.repeatDelay.collect(::updateRepeatInterval) }
                launch { viewModel.swipeDuration.collect(::updateSwipePressDuration) }

                launch { viewModel.intervalUnit.collect(viewBinding.interval.timeUnitField::setSelectedItem) }
                launch { viewModel.swipeDurationUnit.collect(viewBinding.swipeDuration.timeUnitField::setSelectedItem) }

            }
        }
    }


    private fun validateInput(valueMs: Long, unit: TimeUnitDropDownItem): Boolean {
        return when (unit) {
            TimeUnitDropDownItem.Milliseconds -> valueMs >= 40
            TimeUnitDropDownItem.Seconds -> valueMs >= 1.seconds.inWholeMilliseconds
            TimeUnitDropDownItem.Minutes -> valueMs >= 1.minutes.inWholeMilliseconds
            TimeUnitDropDownItem.Hours -> valueMs >= 1.hours.inWholeMilliseconds
        }
    }

    private fun updateHelperText(view: MaterialTextView, isValid: Boolean, unit: TimeUnitDropDownItem) {

        view.apply {
            text = when (unit) {
                TimeUnitDropDownItem.Milliseconds -> context.getString(R.string.interval_desc_2, "40ms")
                TimeUnitDropDownItem.Seconds -> context.getString(R.string.interval_desc_2, "1sec")
                TimeUnitDropDownItem.Minutes -> context.getString(R.string.interval_desc_2, "1min")
                TimeUnitDropDownItem.Hours -> context.getString(R.string.interval_desc_2, "1hr")
            }
            beGoneIf(isValid)
        }
    }

    private fun updateSaveAction(isValidConfig: Boolean) {
        viewBinding.actionButtons.actionDone.isEnabled = isValidConfig
    }

    private fun onSaveButtonClicked() {
        viewModel.getEditedSwipe()?.let { editedSwipe ->
            onConfirmClicked(editedSwipe)
            back()
        }
    }

    private fun onDismissButtonClicked() {
        onDismissClicked()
        back()
    }

    private fun updateRepeatInterval(delay: String) = viewBinding.interval.fieldText.setText(delay, InputType.TYPE_CLASS_NUMBER)

    private fun updateSwipePressDuration(duration: String) = viewBinding.swipeDuration.fieldText.setText(duration, InputType.TYPE_CLASS_NUMBER)
}