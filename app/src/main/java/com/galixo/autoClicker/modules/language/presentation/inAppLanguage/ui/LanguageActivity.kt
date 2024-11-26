package com.galixo.autoClicker.modules.language.presentation.inAppLanguage.ui

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.extensions.beVisibleIf
import com.google.android.material.snackbar.Snackbar
import com.galixo.autoClicker.databinding.ActivityLanguageBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.adapter.AdapterInAppLanguage
import com.galixo.autoClicker.modules.language.presentation.inAppLanguage.viewmodels.InAppLanguageViewModel
import com.galixo.autoClicker.modules.permissions.ui.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    private val viewModel by viewModels<InAppLanguageViewModel>()

    private val adapter by lazy { AdapterInAppLanguage(::updateLanguage) }

    override fun onCreated() {
        initObservers()
        onClickListener()
    }

    private fun initObservers() {
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.languages)
                    showLoading(state.isLoading)

                    if (state.isLanguageApplied) {
                        startNextActivity()
                    }

                    state.errorMessage?.let { showSnackBar(it) }
                }
            }
        }
    }

    private fun onClickListener() {
        binding.continueButton.setOnClickListener {
            applyLanguage()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.beVisibleIf(isLoading)
    }

    private fun startNextActivity() {
        val intent = Intent(this, PermissionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {
                viewModel.fetchLanguages()
            }.show()
    }

    private fun applyLanguage() {
        viewModel.applyLanguage()
    }

    private fun updateLanguage(selectedCode: String) {
        viewModel.updateLanguage(selectedCode)
    }

}