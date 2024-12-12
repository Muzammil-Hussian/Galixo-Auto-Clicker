package com.galixo.autoClicker.core.common.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.common.theme.data.ThemeDataRepository
import com.galixo.autoClicker.core.common.theme.model.UserEditableTheme
import com.galixo.autoClicker.core.common.theme.utils.DarkThemeConfig
import com.galixo.autoClicker.core.common.theme.utils.ThemeBrand
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
            .userEditableTheme
            .map { ThemeUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                initialValue = ThemeUiState.Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch(Dispatchers.IO) {
            themeDataRepository.setThemeBrand(themeBrand)
        }
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            themeDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateGradientColorsPreference(useGradientColors: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            themeDataRepository.setGradientColorsPreference(useGradientColors)
        }
    }
}

sealed interface ThemeUiState {
    data object Loading : ThemeUiState
    data class Success(val theme: UserEditableTheme) : ThemeUiState
}