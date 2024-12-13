package com.galixo.autoClicker.core.common.theme.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


class DataStoreManager(private val context: Context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = PrefTheme.PREFERENCE_DATA_STORE)

    fun getThemeFromDataStore() = context.datastore.data.map {
        when (it[darkThemeKey]) {
            PrefTheme.DARK_THEME_LIGHT -> ThemeConfig.LIGHT
            PrefTheme.DARK_THEME_DARK -> ThemeConfig.DARK
            else -> ThemeConfig.FOLLOW_SYSTEM
        }
    }

    suspend fun saveDarkThemeConfigDataStore(darkThemeConfig: ThemeConfig) {
        context.datastore.edit {
            it[darkThemeKey] = when (darkThemeConfig) {
                ThemeConfig.LIGHT -> PrefTheme.DARK_THEME_LIGHT
                ThemeConfig.DARK -> PrefTheme.DARK_THEME_DARK
                else -> PrefTheme.DARK_THEME_FOLLOW_SYSTEM
            }
        }
    }

    internal companion object {
        val darkThemeKey = intPreferencesKey(PrefTheme.DARK_THEME_KEY)
    }
}