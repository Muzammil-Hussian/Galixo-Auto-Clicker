package com.galixo.autoClicker.core.common.theme.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.extensions.beGone
import com.galixo.autoClicker.core.common.base.extensions.beVisible
import com.galixo.autoClicker.core.common.theme.R
import com.galixo.autoClicker.core.common.theme.databinding.DialogThemeBinding
import com.galixo.autoClicker.core.common.theme.utils.ThemeConfig
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

    private var selectedConfig = ThemeConfig.FOLLOW_SYSTEM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.themeUiState.collectLatest { themeUiState ->
                        when (themeUiState) {
                            is ThemeUiState.Loading -> {
                                viewBinding.loadingTextView.beVisible()
                            }

                            is ThemeUiState.Success -> {
                                viewBinding.loadingTextView.beGone()
                                updateThemePanel(themeUiState.theme)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogThemeBinding.inflate(layoutInflater).apply {

            darkThemeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                selectedConfig = when (checkedId) {
                    R.id.darkThemeSystemDefault -> ThemeConfig.FOLLOW_SYSTEM
                    R.id.darkThemeLight -> ThemeConfig.LIGHT
                    R.id.darkThemeDark -> ThemeConfig.DARK
                    else -> ThemeConfig.FOLLOW_SYSTEM
                }
            }

            applyThemeButton.setOnClickListener {
                viewModel.updateDarkThemeConfig(selectedConfig)
                viewModel.applyTheme(selectedConfig)
                dismiss()
            }
        }


        return MaterialAlertDialogBuilder(requireContext())
            .setView(viewBinding.root)
            .create()
    }

    private fun updateThemePanel(theme: ThemeConfig) {
        when (theme) {
            ThemeConfig.FOLLOW_SYSTEM -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeSystemDefault)
            ThemeConfig.LIGHT -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeLight)
            ThemeConfig.DARK -> viewBinding.darkThemeRadioGroup.check(R.id.darkThemeDark)
        }
    }

    companion object {
        const val TAG = "THEME_DIALOG_FRAGMENT"
    }
}
