package com.galixo.autoClicker.modules.language.presentation.inAppLanguage.viewmodels

import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage

data class LanguageUiState(
    val languages: List<ItemLanguage> = emptyList(),
    val isLoading: Boolean = false,
    val isLanguageApplied: Boolean = false,
    val errorMessage: String? = null
)
