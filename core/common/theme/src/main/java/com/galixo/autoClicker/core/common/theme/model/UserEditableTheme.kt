package com.galixo.autoClicker.core.common.theme.model

import com.galixo.autoClicker.core.common.theme.utils.DarkThemeConfig
import com.galixo.autoClicker.core.common.theme.utils.ThemeBrand

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableTheme(
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useGradientColors: Boolean,
)
