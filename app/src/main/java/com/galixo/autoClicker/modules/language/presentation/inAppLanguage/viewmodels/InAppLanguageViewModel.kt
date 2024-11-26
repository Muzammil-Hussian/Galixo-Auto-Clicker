package com.galixo.autoClicker.modules.language.presentation.inAppLanguage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage
import com.galixo.autoClicker.modules.language.domain.usecases.Result
import com.galixo.autoClicker.modules.language.domain.usecases.UseCaseInAppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InAppLanguageViewModel @Inject constructor(
    private val useCaseInAppLanguage: UseCaseInAppLanguage
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> get() = _uiState

    init {
        fetchLanguages()
    }

    fun fetchLanguages() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        when (val result = useCaseInAppLanguage.fetchLanguages()) {
            is Result.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
            is Result.Success -> setLanguages(result.data)
            is Result.Error -> setError(result.message)
        }
    }


    private fun setLanguages(languages: List<ItemLanguage>, isLoading: Boolean = false) {
        _uiState.value = _uiState.value.copy(languages = languages, isLoading = isLoading)
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
    }

    fun updateLanguage(selectedCode: String) {
        viewModelScope.launch {
            val updatedLanguages = useCaseInAppLanguage.updateLanguages(selectedCode)
            _uiState.value = _uiState.value.copy(languages = updatedLanguages)
        }
    }

    fun applyLanguage() {
        viewModelScope.launch {
            val isApplied = useCaseInAppLanguage.applyLanguage()
            _uiState.value = _uiState.value.copy(isLanguageApplied = isApplied)
        }
    }
}

