package com.galixo.autoClicker.modules.settings

import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.DropdownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setSelectedItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.timeUnitDropdownItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.toDurationMs
import com.galixo.autoClicker.core.common.ui.bindings.fields.onEditorActionListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.databinding.IncludeDialogViewBinding
import com.galixo.autoClicker.databinding.FragmentSettingBinding
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui.LanguageDialogFragment
import com.galixo.autoClicker.modules.settings.Settings.Companion.MAX_LENGTH
import com.galixo.autoClicker.modules.settings.dialog.repeatMode.RepeatModeDialog
import com.galixo.autoClicker.modules.settings.dialog.repeatMode.enum.RepeatMode
import com.galixo.autoClicker.utils.PreferenceUtils
import com.galixo.autoClicker.utils.extensions.openEmailApp
import com.galixo.autoClicker.utils.extensions.openPlayStoreApp
import com.galixo.autoClicker.utils.extensions.openWebUrl
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel: SettingViewModel by viewModels()

    override fun onViewCreated() {
        onClickListener()
        setupObservers()
        setupCardViews()
    }


    internal fun handleBackPress() {
        with(viewModel) {
            viewModel.saveIntervalValue(intervalValue.value)
            viewModel.saveSwipeDurationValue(swipeDurationValue.value)
        }
        findNavController().navigateUp()
    }

    private fun onClickListener() {
        binding.apply {
            languageButton.setOnClickListener { LanguageDialogFragment().show(childFragmentManager, LanguageDialogFragment.LANGUAGE_DIALOG) }
            rateUs.setOnClickListener { context?.openPlayStoreApp() }
            feedback.setOnClickListener { context?.openEmailApp(R.string.galixo_ai_email) }
            privacyPolicy.setOnClickListener { requireContext().openWebUrl(R.string.galixo_ai_privacy_policy_url) }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.intervalDuration.collect(::updateIntervalDuration) }
                launch { viewModel.selectedIntervalTimeUnit.collect(::updateIntervalDurationTimeUnit) }
                launch { viewModel.swipeDuration.collect(::updateSwipeDuration) }
                launch { viewModel.selectedSwipeDurationTimeUnit.collect(::updateSwipeDurationTimeUnit) }
            }
        }
    }

    private fun setupCardViews() {
        binding.apply {
            selectedLanguage.text = PreferenceUtils.selectedLanguage

            with(interval) {
                setupBackgroundDrawable()
                label.setText(R.string.interval_label)
                fieldText.apply {
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getSelectedIntervalUnitTime()) ?: 100L
                        viewModel.setIntervalValue(convertedValue)

                        val isValid = viewModel.validateInput(convertedValue, viewModel.getSelectedIntervalUnitTime())
                        updateHelperText("40 ms", helperText, isValid, viewModel.getSelectedIntervalUnitTime())
                    }
                    onEditorActionListener {
                        val enteredValue = it.toLongOrNull()
                        val valueToSave = enteredValue?.toDurationMs(viewModel.getSelectedIntervalUnitTime()) ?: 100L
                        Log.i(TAG, "setupCardViews: interval enteredValue: $enteredValue and valueToSave: $valueToSave")
                        viewModel.saveIntervalValue((if (valueToSave >= 100) valueToSave else 100))
                    }
                }
                timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                    viewModel.setSelectedIntervalTimeUnit(selectedUnit)
                    val isValid = viewModel.validateInput(viewModel.intervalValue.value, selectedUnit)

                    updateHelperText("40 ms", helperText, isValid, selectedUnit)
                })
            }

            with(swipeDuration) {
                setupBackgroundDrawable()
                label.setText(R.string.swipe_duration)
                fieldText.apply {
                    textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                    setOnTextChangedListener {
                        val enteredValue = it.toString().toLongOrNull()
                        val convertedValue = enteredValue?.toDurationMs(viewModel.getSelectedSwipeDurationTimeUnit()) ?: 350L
                        viewModel.setSwipeDurationValue(convertedValue)

                        val isValid = viewModel.validateInput(convertedValue, viewModel.getSelectedSwipeDurationTimeUnit())
                        updateHelperText("350 ms", helperText, isValid, viewModel.getSelectedSwipeDurationTimeUnit())
                    }
                    onEditorActionListener {
                        val enteredValue = it.toLongOrNull()
                        val valueToSave = enteredValue?.toDurationMs(viewModel.getSelectedSwipeDurationTimeUnit()) ?: 350L
                        Log.i(TAG, "setupCardViews: swipeDuration: $enteredValue and valueToSave: $valueToSave")
                        viewModel.saveSwipeDurationValue(if (valueToSave >= 300) valueToSave else 300)
                    }
                }
                timeUnitField.setItems(items = timeUnitDropdownItems, onItemSelected = { selectedUnit ->
                    viewModel.setSwipeDurationTimeUnit(selectedUnit)
                    val isValid = viewModel.validateInput(viewModel.swipeDurationValue.value, selectedUnit)

                    updateHelperText("350 ms", helperText, isValid, selectedUnit)
                })
            }
        }


        binding.repeatMode.textField.apply {

            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            setText(R.string.never_stop)

            setOnClickListener {
                RepeatModeDialog { mode: RepeatMode, duration: Long, repeats: Int ->
                    Log.i(TAG, "setupCardViews: mode: $mode, duration: $duration and repeatCycles: $repeats")
                    val text = when (mode) {
                        RepeatMode.NEVER_STOP -> getString(R.string.never_stop)
                        RepeatMode.STOP_AFTER_DURATION -> "$duration Ms"
                        RepeatMode.STOP_AFTER_REPEATS -> "$repeats Reps"
                    }
                    setText(text)

                }.show(requireActivity().supportFragmentManager, RepeatModeDialog.REPEAT_MODE_DIALOG_SETTINGS)
            }
        }

        binding.switchAntiDetection.isChecked = viewModel.getRandomization()
        binding.switchAntiDetection.setOnCheckedChangeListener { _, isChecked -> viewModel.setRandomization(isChecked) }
    }

    private fun IncludeDialogViewBinding.setupBackgroundDrawable() {
        dialogViewRoot.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_round_16_dp)
        val topBottomMargin = (8 * resources.displayMetrics.density).toInt()
        val horizontalPadding = (16 * resources.displayMetrics.density).toInt()
        val layoutParams = dialogViewRoot.layoutParams as LinearLayout.LayoutParams
        layoutParams.setMargins(0, topBottomMargin, 0, topBottomMargin)
        dialogViewRoot.setPadding(horizontalPadding)
        dialogViewRoot.layoutParams = layoutParams
    }

    private fun updateHelperText(millisecondsValue: String, helperTextView: MaterialTextView, isValid: Boolean, unit: TimeUnitDropDownItem) {
        helperTextView.apply {
            text = when (unit) {
                TimeUnitDropDownItem.Milliseconds -> getString(R.string.interval_desc_2, millisecondsValue)
                TimeUnitDropDownItem.Seconds -> getString(R.string.interval_desc_2, "1s")
                TimeUnitDropDownItem.Minutes -> getString(R.string.interval_desc_2, "1m")
                TimeUnitDropDownItem.Hours -> getString(R.string.interval_desc_2, "1h")
            }

            beGoneIf(isValid)
        }
    }

    private fun updateIntervalDuration(duration: String) = binding.interval.fieldText.setText(duration, InputType.TYPE_CLASS_NUMBER)
    private fun updateIntervalDurationTimeUnit(item: DropdownItem) = binding.interval.timeUnitField.setSelectedItem(item = item)
    private fun updateSwipeDuration(duration: String) = binding.swipeDuration.fieldText.setText(duration, InputType.TYPE_CLASS_NUMBER)
    private fun updateSwipeDurationTimeUnit(item: DropdownItem) = binding.swipeDuration.timeUnitField.setSelectedItem(item = item)

}

private const val TAG = "SettingFragment"