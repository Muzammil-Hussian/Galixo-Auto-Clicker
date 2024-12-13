package com.galixo.autoClicker.core.common.theme.viewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.common.theme.data.ThemeDataRepository
import com.galixo.autoClicker.core.common.theme.utils.ThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeDataRepository: ThemeDataRepository,
) : ViewModel() {

    val themeUiState: StateFlow<ThemeUiState> =
        themeDataRepository
            .userThemeConfig
            .map { ThemeUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                initialValue = ThemeUiState.Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )


    fun updateDarkThemeConfig(darkThemeConfig: ThemeConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            themeDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun applyTheme(darkThemeConfig: ThemeConfig) {
        val mode = when (darkThemeConfig) {
            ThemeConfig.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeConfig.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}

sealed interface ThemeUiState {
    data object Loading : ThemeUiState
    data class Success(val theme: ThemeConfig) : ThemeUiState
}