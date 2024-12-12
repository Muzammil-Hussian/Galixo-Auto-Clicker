package com.galixo.autoClicker.core.common.theme.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.theme.R
import com.galixo.autoClicker.core.common.theme.databinding.DialogThemeBinding
import com.galixo.autoClicker.core.common.theme.model.UserEditableTheme
import com.galixo.autoClicker.core.common.theme.utils.DarkThemeConfig
import com.galixo.autoClicker.core.common.theme.utils.ThemeBrand
import com.galixo.autoClicker.core.common.theme.viewModel.ThemeUiState
import com.galixo.autoClicker.core.common.theme.viewModel.ThemeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ThemeDialogFragment : DialogFragment() {
    /** ViewBinding for this dialog. */
    private lateinit var viewBinding: DialogThemeBinding
    private val viewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.themeUiState.collectLatest { themeUiState ->
                        when (themeUiState) {
                            is ThemeUiState.Loading -> {
                                viewBinding.loadingTextView.visibility = View.VISIBLE
                            }

                            is ThemeUiState.Success -> {
                                viewBinding.loadingTextView.visibility = View.GONE
                                updateThemePanel(themeUiState.theme)
                            }
                        }
                    }
                }
            }
        }
    }


    companion object {
        const val TAG = "THEME_DIALOG_FRAGMENT"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogThemeBinding.inflate(layoutInflater).apply {
            themeBrandRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.themeBrandDefault -> viewModel.updateThemeBrand(ThemeBrand.DEFAULT)
                    R.id.themeBrandDynamic -> viewModel.updateThemeBrand(ThemeBrand.DYNAMIC)
                }
            }

            gradientColorRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.gradientColorYes -> viewModel.updateGradientColorsPreference(true)
                    R.id.gradientColorNo -> viewModel.updateGradientColorsPreference(false)
                }
            }

            darkThemeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.darkThemeSystemDefault -> viewModel.updateDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
                    R.id.darkThemeLight -> viewModel.updateDarkThemeConfig(DarkThemeConfig.LIGHT)
                    R.id.darkThemeDark -> viewModel.updateDarkThemeConfig(DarkThemeConfig.DARK)
                }
            }
        }


        return MaterialAlertDialogBuilder(requireContext())
            .setView(viewBinding.root)
            .setTitle("App theme")
            .setPositiveButton("ok") { _, _ -> dismiss() }
            .create()
    }

    private fun updateThemePanel(theme: UserEditableTheme) {
        when (theme.themeBrand) {
            ThemeBrand.DEFAULT -> viewBinding.themeBrandRadioGroup.check(R.id.themeBrandDefault)
            ThemeBrand.DYNAMIC -> viewBinding.themeBrandRadioGroup.check(R.id.themeBrandDynamic)
        }

        if (theme.useGradientColors) {
            viewBinding.gradientColorRadioGroup.check(R.id.gradientColorYes)
        } else {
            viewBinding.gradientColorRadioGroup.check(R.id.gradientColorNo)
        }

        when (theme.darkThemeConfig) {
            DarkThemeConfig.FOLLOW_SYSTEM -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeSystemDefault)
            DarkThemeConfig.LIGHT -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeLight)
            DarkThemeConfig.DARK -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeDark)
        }
    }

}
