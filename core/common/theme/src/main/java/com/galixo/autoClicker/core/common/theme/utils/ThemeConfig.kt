package com.galixo.autoClicker.core.common.theme.utils


enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

enum class ThemeBrand {
    DEFAULT, DYNAMIC
}

object ThemeConfig {

    // Theme String
    const val TITLE = "App Theme"
    const val LOADING = "Loading…"
    const val OK = "OK"
    const val BRAND_DEFAULT = "Default"
    const val BRAND_DYNAMIC = "Dynamic"
    const val USE_GRADIENT_COLORS = "Use Gradient Colors"
    const val YES = "Yes"
    const val NO = "No"
    const val DARK_MODE_PREFERENCE = "Dark mode preference"
    const val SYSTEM_DEFAULT = "System default"
    const val LIGHT = "Light"
    const val DARK = "Dark"

    object PrefTheme {
        const val PREFERENCE_DATA_STORE = "Material_Theme"

        const val THEME_BRAND_KEY = "Material_Theme_themeBrandKey"
        const val DARK_THEME_KEY = "Material_Theme_darkThemeKey"
        const val USE_GRADIENT_COLORS_KEY = "Material_Theme_useGradientColorsKey"

        const val THEME_BRAND_DEFAULT = 0
        const val THEME_BRAND_DYNAMIC = 1

        const val DARK_THEME_FOLLOW_SYSTEM = 0
        const val DARK_THEME_LIGHT = 1
        const val DARK_THEME_DARK = 2


    }
}