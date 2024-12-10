package com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.databinding.DialogLanguageScreenSettingsBinding
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.adapter.AdapterInAppLanguage
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.viewmodels.InAppLanguageViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageDialogFragment : DialogFragment() {

    internal companion object {
        internal const val LANGUAGE_DIALOG = "LanguageDialogFragment"
    }

    private lateinit var viewBinding: DialogLanguageScreenSettingsBinding

    private val viewModel: InAppLanguageViewModel by viewModels()

    private val adapter: AdapterInAppLanguage by lazy { AdapterInAppLanguage(::updateLanguage) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        adapter.submitList(state.languages)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogLanguageScreenSettingsBinding.inflate(layoutInflater).apply {
            recyclerView.adapter = adapter
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language)
            .setView(viewBinding.root)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.apply) { _, _ ->

                viewModel.applyLanguage()
                dismiss()
            }
            .create()
    }

    private fun updateLanguage(selectedCode: String) {
        viewModel.updateLanguage(selectedCode)
    }
}