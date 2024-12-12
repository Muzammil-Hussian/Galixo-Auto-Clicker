package com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig

import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.bottomSheet.OverlayDialogSheet
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.DropdownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setSelectedItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.timeUnitDropdownItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.toDurationMs
import com.galixo.autoClicker.core.common.ui.bindings.fields.onEditorActionListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.DialogScenarioConfigBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.repeatMode.RepeatModeDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class ScenarioConfigDialog(
    private val onConfigSaved: () -> Unit,
    private val onConfigDiscarded: () -> Unit,
) : OverlayDialogSheet(R.style.AppTheme) {

    /** View model for the container dialog. */
    private val viewModel: ScenarioConfigViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { scenarioConfigViewModel() },
    )

    private lateinit var viewBinding: DialogScenarioConfigBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogScenarioConfigBinding.inflate(LayoutInflater.from(context))
        return viewBinding.root
    }

    override fun onDialogCreated(dialog: BottomSheetDialog) {
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.isValid.collect(::updateDoneButtonState) }
                launch { viewModel.intervalDuration.collect(::updateIntervalDuration) }
                launch { viewModel.selectedIntervalTimeUnit.collect(::updateIntervalDurationTimeUnit) }
                launch { viewModel.swipeDuration.collect(::updateSwipeDuration) }
                launch { viewModel.selectedSwipeDurationTimeUnit.collect(::updateSwipeDurationTimeUnit) }
                launch { viewModel.repeatMode.collect(viewBinding.repeatMode.textField::setText) }

            }
        }
    }

    private fun setupViews() {
        viewBinding.apply {
            setupActionButtons()
            with(interval) {
                label.setText(R.string.interval_label)
                fieldText.apply {
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getSelectedIntervalUnitTime()) ?: 0L
                        viewModel.setIntervalValue(convertedValue)

                        val isValid = viewModel.validateInput(false, convertedValue, viewModel.getSelectedIntervalUnitTime())
                        updateHelperText("40 ms", helperText, isValid, viewModel.getSelectedIntervalUnitTime())
                    }
                    onEditorActionListener {
                        val enteredValue = it.toLongOrNull()
                        val valueToSave = enteredValue?.toDurationMs(viewModel.getSelectedIntervalUnitTime()) ?: 100L
                        viewModel.saveIntervalValue((if (valueToSave >= 100) valueToSave else 100))
                    }
                    hideSoftInputOnFocusLoss(textField)
                }
                timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                    viewModel.setSelectedIntervalTimeUnit(selectedUnit)
                    val isValid = viewModel.validateInput(false, viewModel.intervalValue.value, selectedUnit)
                    updateHelperText("40 ms", helperText, isValid, selectedUnit)
                })
            }

            with(swipeDuration) {
                label.setText(R.string.swipe_duration)
                fieldText.apply {
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getSelectedSwipeDurationTimeUnit()) ?: 0L
                        viewModel.setSwipeDurationValue(convertedValue)
                        val isValid = viewModel.validateInput(true, convertedValue, viewModel.getSelectedSwipeDurationTimeUnit())
                        updateHelperText("350 ms", helperText, isValid, viewModel.getSelectedSwipeDurationTimeUnit())
                    }
                    onEditorActionListener {
                        val enteredValue = it.toLongOrNull()
                        val valueToSave = enteredValue?.toDurationMs(viewModel.getSelectedSwipeDurationTimeUnit()) ?: 100L
                        viewModel.saveSwipeDurationValue(if (valueToSave >= 300) valueToSave else 300)
                    }
                    hideSoftInputOnFocusLoss(textField)
                }
                timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                    viewModel.setSwipeDurationTimeUnit(selectedUnit)

                    val isValid = viewModel.validateInput(true, viewModel.swipeDurationValue.value, selectedUnit)
                    updateHelperText("350 ms", helperText, isValid, selectedUnit)
                })
            }
        }

        viewBinding.repeatMode.textField.apply {
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))

            setDebouncedOnClickListener {
                overlayManager.navigateTo(
                    context = context,
                    newOverlay = RepeatModeDialog(
                        onConfigDiscarded = {},
                        onConfigSaved = {}
                    ),
                    hideCurrent = false
                )
            }
        }

        viewBinding.switchAntiDetection.isChecked = viewModel.getRandomization()
        viewBinding.switchAntiDetection.setOnCheckedChangeListener { _, isChecked -> viewModel.setRandomization(isChecked) }
    }

    private fun updateHelperText(millisecondsValue: String, helperTextView: MaterialTextView, isValid: Boolean, unit: TimeUnitDropDownItem) {
        helperTextView.apply {
            text = when (unit) {
                TimeUnitDropDownItem.Milliseconds -> context.getString(R.string.interval_desc_2, millisecondsValue)
                TimeUnitDropDownItem.Seconds -> context.getString(R.string.interval_desc_2, "1s")
                TimeUnitDropDownItem.Minutes -> context.getString(R.string.interval_desc_2, "1m")
                TimeUnitDropDownItem.Hours -> context.getString(R.string.interval_desc_2, "1h")
            }
            beGoneIf(isValid)
        }
    }


    private fun updateDoneButtonState(isEnabled: Boolean) {
        viewBinding.actionButtons.actionDone.isEnabled = isEnabled
    }

    private fun updateIntervalDuration(duration: String) = viewBinding.interval.fieldText.setText(duration, InputType.TYPE_CLASS_NUMBER)
    private fun updateIntervalDurationTimeUnit(item: DropdownItem) = viewBinding.interval.timeUnitField.setSelectedItem(item = item)
    private fun updateSwipeDuration(duration: String) = viewBinding.swipeDuration.fieldText.setText(duration, InputType.TYPE_CLASS_NUMBER)
    private fun updateSwipeDurationTimeUnit(item: DropdownItem) = viewBinding.swipeDuration.timeUnitField.setSelectedItem(item = item)

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
                with(viewModel) {
                    saveIntervalValue(intervalValue.value)
                    saveSwipeDurationValue(swipeDurationValue.value)
                }
                onConfigSaved.invoke()
                back()
            }
        }
    }
}

private const val MAX_LENGTH = 6

enum class ButtonAction { CANCEL, DONE }
