package com.galixo.autoClicker.core.common.theme.data

import android.content.Context
import com.galixo.autoClicker.core.common.theme.utils.DataStoreManager
import com.galixo.autoClicker.core.common.theme.utils.ThemeConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ThemeDataRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStoreManager = DataStoreManager(context)
    val userThemeConfig = dataStoreManager.getThemeFromDataStore()
    suspend fun setDarkThemeConfig(darkThemeConfig: ThemeConfig) = dataStoreManager.saveDarkThemeConfigDataStore(darkThemeConfig)
}
