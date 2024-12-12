package com.galixo.autoClicker.core.common.theme.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.galixo.autoClicker.core.common.theme.model.UserEditableTheme
import kotlinx.coroutines.flow.map


class DataStoreManager(private val context: Context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = ThemeConfig.PrefTheme.PREFERENCE_DATA_STORE)

    companion object {
        val themeBrandKey = intPreferencesKey(ThemeConfig.PrefTheme.THEME_BRAND_KEY)
        val darkThemeKey = intPreferencesKey(ThemeConfig.PrefTheme.DARK_THEME_KEY)
        val useGradientColorsKey = booleanPreferencesKey(ThemeConfig.PrefTheme.USE_GRADIENT_COLORS_KEY)
    }

    fun getThemeFromDataStore() = context.datastore.data.map {
        UserEditableTheme(
            themeBrand = when (it[themeBrandKey]) {
                ThemeConfig.PrefTheme.THEME_BRAND_DYNAMIC -> ThemeBrand.DYNAMIC
                else -> ThemeBrand.DEFAULT
            },
            darkThemeConfig = when (it[darkThemeKey]) {
                ThemeConfig.PrefTheme.DARK_THEME_LIGHT -> DarkThemeConfig.LIGHT
                ThemeConfig.PrefTheme.DARK_THEME_DARK -> DarkThemeConfig.DARK
                else -> DarkThemeConfig.FOLLOW_SYSTEM
            },
            useGradientColors = it[useGradientColorsKey] ?: true
        )
    }

    suspend fun saveThemeBrandDataStore(themeBrand: ThemeBrand) {
        context.datastore.edit {
            it[themeBrandKey] = when (themeBrand) {
                ThemeBrand.DYNAMIC -> ThemeConfig.PrefTheme.THEME_BRAND_DYNAMIC
                else -> ThemeConfig.PrefTheme.THEME_BRAND_DEFAULT
            }
        }
    }

    suspend fun saveDarkThemeConfigDataStore(darkThemeConfig: DarkThemeConfig) {
        context.datastore.edit {
            it[darkThemeKey] = when (darkThemeConfig) {
                DarkThemeConfig.LIGHT -> ThemeConfig.PrefTheme.DARK_THEME_LIGHT
                DarkThemeConfig.DARK -> ThemeConfig.PrefTheme.DARK_THEME_DARK
                else -> ThemeConfig.PrefTheme.DARK_THEME_FOLLOW_SYSTEM
            }
        }
    }

    suspend fun saveGradientColorsPreferenceDataStore(useGradientColors: Boolean) {
        context.datastore.edit {
            it[useGradientColorsKey] = useGradientColors
        }
    }

}