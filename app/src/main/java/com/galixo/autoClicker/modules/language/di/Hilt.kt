package com.galixo.autoClicker.modules.language.di


import com.galixo.autoClicker.modules.language.data.repository.ILanguageRepository
import com.galixo.autoClicker.modules.language.data.repository.LanguageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LanguageModule {

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(languageRepository: LanguageRepository): ILanguageRepository
}
