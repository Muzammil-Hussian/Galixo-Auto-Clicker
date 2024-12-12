package com.galixo.autoClicker.core.common.theme.model

import com.galixo.autoClicker.core.common.theme.utils.ThemeConfig

data class ThemeString(
    val title: String = ThemeConfig.TITLE,
    val loading: String = ThemeConfig.LOADING,
    val ok: String = ThemeConfig.OK,
    val brandDefault: String = ThemeConfig.BRAND_DEFAULT,
    val brandDynamic: String = ThemeConfig.BRAND_DYNAMIC,
    val useGradientColors: String = ThemeConfig.USE_GRADIENT_COLORS,
    val yes: String = ThemeConfig.YES,
    val no: String = ThemeConfig.NO,
    val darkModePreference: String = ThemeConfig.DARK_MODE_PREFERENCE,
    val systemDefault: String = ThemeConfig.SYSTEM_DEFAULT,
    val light: String = ThemeConfig.LIGHT,
    val dark: String = ThemeConfig.DARK,
)
