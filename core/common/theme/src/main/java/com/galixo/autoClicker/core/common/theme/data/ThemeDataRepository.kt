package com.galixo.autoClicker.core.common.theme.data

import android.content.Context
import com.galixo.autoClicker.core.common.theme.utils.DarkThemeConfig
import com.galixo.autoClicker.core.common.theme.utils.DataStoreManager
import com.galixo.autoClicker.core.common.theme.utils.ThemeBrand
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ThemeDataRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private val dataStoreManager = DataStoreManager(context)

    val userEditableTheme = dataStoreManager.getThemeFromDataStore()

    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        dataStoreManager.saveThemeBrandDataStore(themeBrand)
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        dataStoreManager.saveDarkThemeConfigDataStore(darkThemeConfig)
    }

    suspend fun setGradientColorsPreference(useGradientColors: Boolean) {
        dataStoreManager.saveGradientColorsPreferenceDataStore(useGradientColors)
    }
}