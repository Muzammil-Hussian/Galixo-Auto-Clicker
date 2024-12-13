package com.galixo.autoClicker.core.common.theme.utils


enum class ThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

object PrefTheme {
    const val PREFERENCE_DATA_STORE = "Material_Theme"
    const val DARK_THEME_KEY = "Material_Theme_darkThemeKey"
    const val DARK_THEME_FOLLOW_SYSTEM = 0
    const val DARK_THEME_LIGHT = 1
    const val DARK_THEME_DARK = 2
}
