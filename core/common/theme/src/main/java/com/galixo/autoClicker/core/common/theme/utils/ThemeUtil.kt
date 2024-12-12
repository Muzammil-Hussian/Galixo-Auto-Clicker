package com.galixo.autoClicker.core.common.theme.utils

object ThemeUtil {

   /* *//**
     * Returns `ThemeBrand` if the Other theme should be used, as a function of the [themeUiState].
     *//*
    @Composable
    fun shouldUseOtherThemeBrand(
        themeUiState: ThemeUiState,
    ): ThemeBrand = when (themeUiState) {
        Loading -> ThemeBrand.DEFAULT
        is Success -> themeUiState.theme.themeBrand
    }

    *//**
     * Returns `true` if the Gradient colors is disabled, as a function of the [themeUiState].
     *//*
    @Composable
    fun shouldDisableGradientColors(
        themeUiState: ThemeUiState,
    ): Boolean = when (themeUiState) {
        Loading -> false
        is Success -> !themeUiState.theme.useGradientColors
    }

    *//**
     * Returns `true` if dark theme should be used, as a function of the [themeUiState] and the
     * current system context.
     *//*
    @Composable
    fun shouldUseDarkTheme(
        themeUiState: ThemeUiState,
    ): Boolean = when (themeUiState) {
        Loading -> isSystemInDarkTheme()
        is Success -> when (themeUiState.theme.darkThemeConfig) {
            DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            DarkThemeConfig.LIGHT -> false
            DarkThemeConfig.DARK -> true
        }
    }*/

}