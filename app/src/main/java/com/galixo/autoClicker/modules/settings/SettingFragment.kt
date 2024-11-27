package com.galixo.autoClicker.modules.settings

import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setItems
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.setSelectedItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.timeUnitDropdownItems
import com.galixo.autoClicker.core.common.ui.bindings.fields.onEditorActionListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.databinding.IncludeDialogViewBinding
import com.galixo.autoClicker.databinding.FragmentSettingBinding
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui.LanguageDialogFragment
import com.galixo.autoClicker.modules.settings.Settings.Companion.MAX_LENGTH
import com.galixo.autoClicker.utils.PreferenceUtils
import com.galixo.autoClicker.utils.extensions.openEmailApp
import com.galixo.autoClicker.utils.extensions.openPlayStoreApp
import com.galixo.autoClicker.utils.extensions.openWebUrl
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel by viewModels<SettingViewModel>()

    override fun onViewCreated() {
        setupObservers()
        setupCardViews()
        onClickListener()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.intervalValue.collect { binding.interval.fieldText.setText(it) } }
                launch {
                    viewModel.intervalTimeUnit.collect {
                        binding.interval.timeUnitField.setSelectedItem(
                            it
                        )
                    }
                }
                launch {
                    viewModel.intervalError.collect {
                        updateHelperText(
                            binding.interval.helperText,
                            it,
                            viewModel.intervalTimeUnit.value
                        )
                    }
                }

                launch {
                    viewModel.swipeDurationValue.collect {
                        binding.swipeDuration.fieldText.setText(
                            it
                        )
                    }
                }
                launch {
                    viewModel.swipeDurationTimeUnit.collect {
                        binding.swipeDuration.timeUnitField.setSelectedItem(
                            it
                        )
                    }
                }
                launch {
                    viewModel.swipeDurationError.collect {
                        updateHelperText(
                            binding.swipeDuration.helperText,
                            it,
                            viewModel.swipeDurationTimeUnit.value
                        )
                    }
                }
            }
        }
    }


    private fun setupCardViews() {
        binding.selectedLanguage.text = PreferenceUtils.selectedLanguage
        setupField(
            binding.interval,
            R.string.interval_label,
            viewModel::setIntervalValue,
            viewModel::setIntervalTimeUnit
        )
        setupField(
            binding.swipeDuration,
            R.string.swipe_duration,
            viewModel::setSwipeDurationValue,
            viewModel::setSwipeDurationTimeUnit
        )
        binding.repeatMode.apply {
            root.setOnClickListener {

            }
            textField.setText(R.string.never_stop)
        }
    }

    private fun setupField(
        fieldBinding: IncludeDialogViewBinding,
        titleResId: Int,
        setValue: (Long) -> Unit,
        setUnit: (TimeUnitDropDownItem) -> Unit
    ) {
        fieldBinding.apply {
            label.setText(titleResId)
            fieldText.apply {
                textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                onEditorActionListener {
                    Log.i(TAG, "onEditorActionListener: ${it.toLong()}")
                    setValue.invoke(it.toLong())
                }
                /*  setOnTextChangedListener { text ->
                      val valueMs = text.toString().toLongOrNull()?.toDurationMs(fieldBinding.timeUnitField.getSelectedItem())
                      if (valueMs != null) setValue(valueMs)
                  }*/
            }
            timeUnitField.setItems(
                items = timeUnitDropdownItems,
                onItemSelected = setUnit
            )
        }
    }

    private fun updateHelperText(
        helperText: MaterialTextView,
        isValid: Boolean,
        unit: TimeUnitDropDownItem
    ) {
        helperText.text = when (unit) {
            TimeUnitDropDownItem.Milliseconds -> getString(R.string.interval_desc_2, "40ms")
            TimeUnitDropDownItem.Seconds -> getString(R.string.interval_desc_2, "1s")
            TimeUnitDropDownItem.Minutes -> getString(R.string.interval_desc_2, "1m")
            TimeUnitDropDownItem.Hours -> getString(R.string.interval_desc_2, "1h")
        }
        helperText.beGoneIf(isValid)
    }

    private fun onClickListener() {
        binding.apply {
            languageButton.setOnClickListener {
                LanguageDialogFragment().show(
                    childFragmentManager,
                    LanguageDialogFragment.LANGUAGE_DIALOG
                )
            }
            rateUs.setOnClickListener { context?.openPlayStoreApp() }
            feedback.setOnClickListener { context?.openEmailApp(R.string.galixo_ai_email) }
            privacyPolicy.setOnClickListener { requireContext().openWebUrl("https://galixo.ai/gallery/privacy-policy") }
        }
    }
}

private const val TAG = "SettingFragment"