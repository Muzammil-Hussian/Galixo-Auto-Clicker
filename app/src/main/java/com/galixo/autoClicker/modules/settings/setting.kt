package com.galixo.autoClicker.modules.settings

import android.text.InputFilter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.ui.bindings.fields.onEditorActionListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.common.ui.databinding.IncludeDialogViewBinding
import com.galixo.autoClicker.databinding.FragmentSettingBinding
import com.galixo.autoClicker.feature.config.data.getConfigPreferences
import com.galixo.autoClicker.feature.config.data.putClickRepeatDelayConfig
import com.galixo.autoClicker.feature.config.data.putSwipeRepeatDelayConfig
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeDurationMs
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeRepeatDelay
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui.LanguageDialogFragment
import com.galixo.autoClicker.utils.extensions.openEmailApp
import com.galixo.autoClicker.utils.extensions.openPlayStoreApp
import com.galixo.autoClicker.utils.extensions.openWebUrl

/**
 * Setting fragment old
 */
class Settings : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {


    override fun onViewCreated() {
        initViews()
        onClickListener()
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

    private fun initViews() {
        binding.apply {
            setupCardView(
                interval,
                R.string.interval_label,
                requireContext().getDefaultSwipeRepeatDelay().toString()
            ) {
                requireContext().getConfigPreferences().edit()
                    .putClickRepeatDelayConfig(it.toLong()).apply()
            }
            setupCardView(
                swipeDuration,
                R.string.swipe_duration,
                requireContext().getDefaultSwipeDurationMs().toString()
            ) {
                requireContext().getConfigPreferences().edit()
                    .putSwipeRepeatDelayConfig(it.toLong()).apply()
            }
        }
    }


    private fun setupCardView(
        fieldBinding: IncludeDialogViewBinding,
        titleResId: Int,
        defaultValueResId: String,
        callback: (value: String) -> Unit
    ) {
        with(fieldBinding) {
            applyFilledStyle(root)
            label.setText(titleResId)
            fieldText.apply {
                setText(defaultValueResId)
                textField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
                onEditorActionListener { value -> callback.invoke(value) }
            }
        }
    }

    private fun applyFilledStyle(view: ConstraintLayout) {
        view.apply {
            background = ContextCompat.getDrawable(context, R.drawable.bg_round_16_dp)

        }
    }

    companion object {
        const val MAX_LENGTH = 5
    }
}

