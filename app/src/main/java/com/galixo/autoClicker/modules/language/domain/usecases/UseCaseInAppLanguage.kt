package com.galixo.autoClicker.modules.language.domain.usecases

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.galixo.autoClicker.modules.language.data.repository.ILanguageRepository
import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage
import com.galixo.autoClicker.utils.PreferenceUtils
import javax.inject.Inject


class UseCaseInAppLanguage @Inject constructor(private val iLanguageRepository: ILanguageRepository) {

    private var appliedLanguageCode: String? = null
    private var userSelectedCode: String = "en"

    fun fetchLanguages(): Result<List<ItemLanguage>> = try {
        appliedLanguageCode = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (appliedLanguageCode.isNullOrEmpty()) appliedLanguageCode = "en"

        val allLanguages = iLanguageRepository.getLanguages().map { item ->
            item.copy(isSelected = (item.languageCode == appliedLanguageCode))
        }
        Result.Success(allLanguages)
    } catch (e: Exception) {
        Result.Error("Failed to load languages")
    }


    fun updateLanguages(selectedCode: String): List<ItemLanguage> {
        userSelectedCode = selectedCode
        return iLanguageRepository.getLanguages().map { item ->
            item.copy(isSelected = (item.languageCode == selectedCode))
        }
    }

    fun applyLanguage(): Boolean {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(userSelectedCode)
        AppCompatDelegate.setApplicationLocales(appLocale)

        PreferenceUtils.selectedLanguage = iLanguageRepository.getLanguages().find { it.languageCode == userSelectedCode }?.languageNameTranslated ?: "English"
        return true
    }
}