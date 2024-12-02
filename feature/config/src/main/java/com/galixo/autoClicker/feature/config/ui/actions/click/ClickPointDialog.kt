package com.galixo.autoClicker.feature.config.ui.actions.click

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.core.common.ui.R
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
import com.galixo.autoClicker.feature.config.databinding.DialogConfigClickActionBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ClickPointDialog(
    val click: Action.Click,
    private val onConfirmClicked: (Action.Click) -> Unit,
    private val onDismissClicked: () -> Unit,
) : OverlayDialog(R.style.AppTheme) {

    private val viewModel: ClickPointViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { clickPointViewModel() }
    )

    private lateinit var viewBinding: DialogConfigClickActionBinding

    override fun onCreateView(): ViewGroup {
        viewModel.setEditedClick(click)

        viewBinding = DialogConfigClickActionBinding.inflate(LayoutInflater.from(context)).apply {
            with(interval) {
                label.setText(R.string.interval_description)

                fieldText.apply {
                    textField.filters = arrayOf(MinMaxInputFilter(REPEAT_DELAY_MIN_MS.toInt(), REPEAT_DELAY_MAX_MS.toInt()))

                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getTimeUnit()) ?: 0L
                        viewModel.setRepeatDelay(convertedValue)

                        val isValid = validateInput(convertedValue, viewModel.getTimeUnit())
                        updateHelperText(isValid, viewModel.getTimeUnit())
                        updateSaveAction(isValid)
                    }
                }

                timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                    viewModel.setTimeUnit(selectedUnit)
                    val isValid = validateInput(viewModel.getRepeatDelayMs(), selectedUnit)
                    updateHelperText(isValid, selectedUnit)
                    updateSaveAction(isValid)
                })
            }

            with(actionButtons) {
                actionCancel.setDebouncedOnClickListener {
                    onDismissClicked.invoke()
                    back()
                }

                actionDone.setDebouncedOnClickListener {
                    viewModel.getEditedClick()?.let { editedClick ->
                        onConfirmClicked.invoke(editedClick)
                        back()
                    }
                }
            }
        }

        return viewBinding.root
    }

    override fun onDialogCreated(dialog: AlertDialog) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.repeatDelay.collect(::updateClickRepeatDelay) }
                launch { viewModel.selectedUnitItem.collect(viewBinding.interval.timeUnitField::setSelectedItem) }
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

    private fun updateHelperText(isValid: Boolean, unit: TimeUnitDropDownItem) {
        viewBinding.interval.helperText.apply {
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

    private fun updateClickRepeatDelay(delay: String) {
        viewBinding.interval.fieldText.setText(delay, InputType.TYPE_CLASS_NUMBER)
    }
}
