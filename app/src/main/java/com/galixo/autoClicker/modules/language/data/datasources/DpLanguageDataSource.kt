package com.galixo.autoClicker.modules.language.data.datasources

import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage
import javax.inject.Inject

class DpLanguageDataSource @Inject constructor() {
    var languageList: ArrayList<ItemLanguage> = arrayListOf(
        ItemLanguage(
            languageCode = "en",
            languageName = "English",
            languageNameTranslated = "English",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "af",
            languageName = "Afrikaans",
            languageNameTranslated = "Afrikaans",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "ar", languageName = "Arabic", languageNameTranslated = "عربي", isSelected = false
        ),
        ItemLanguage(
            languageCode = "bn",
            languageName = "Bengali",
            languageNameTranslated = "বাংলা",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "zh",
            languageName = "Chinese",
            languageNameTranslated = "中國人",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "cs",
            languageName = "Czech",
            languageNameTranslated = "čeština",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "da", languageName = "Danish", languageNameTranslated = "dansk", isSelected = false
        ),
        ItemLanguage(
            languageCode = "nl",
            languageName = "Dutch",
            languageNameTranslated = "Nederlands",
            isSelected = false,
        ),

        ItemLanguage(
            languageCode = "fr",
            languageName = "French",
            languageNameTranslated = "Français",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "de",
            languageName = "German",
            languageNameTranslated = "Deutsch",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "hi", languageName = "Hindi", languageNameTranslated = "हिंदी", isSelected = false
        ),
        ItemLanguage(
            languageCode = "in",
            languageName = "Indonesian",
            languageNameTranslated = "bahasa Indonesia",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "it",
            languageName = "Italian",
            languageNameTranslated = "Italiana",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "ja",
            languageName = "Japanese",
            languageNameTranslated = "日本",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "ko", languageName = "Korean", languageNameTranslated = "한국인", isSelected = false
        ),
        ItemLanguage(
            languageCode = "ms", languageName = "Malay", languageNameTranslated = "Melayu", isSelected = false
        ),
        ItemLanguage(
            languageCode = "fa",
            languageName = "Persian",
            languageNameTranslated = "فارسی",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "pl",
            languageName = "Polish",
            languageNameTranslated = "Polski",
            isSelected = false
        ),
        ItemLanguage(
            languageCode = "ru",
            languageName = "Russian",
            languageNameTranslated = "Русский",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "es",
            languageName = "Spanish",
            languageNameTranslated = "Español",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "sv",
            languageName = "Swedish",
            languageNameTranslated = "svenska",
            isSelected = false,
        ),
        ItemLanguage(
            languageCode = "ta", languageName = "Tamil", languageNameTranslated = "தமிழ்", isSelected = false
        ),
        ItemLanguage(
            languageCode = "vi",
            languageName = "Vietnamese",
            languageNameTranslated = "Tiếng Việt",
            isSelected = false,
        ),
    )
}