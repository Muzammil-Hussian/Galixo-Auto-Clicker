package com.galixo.autoClicker.modules.language.data.repository

import com.galixo.autoClicker.modules.language.data.datasources.DpLanguageDataSource
import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage
import javax.inject.Inject


class LanguageRepository @Inject constructor(private val dpLanguageDataSource: DpLanguageDataSource) : ILanguageRepository {
    override fun getLanguages(): List<ItemLanguage> = dpLanguageDataSource.languageList
}

interface ILanguageRepository {
    fun getLanguages(): List<ItemLanguage>
}