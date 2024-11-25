package com.galixo.autoClicker.core.scenarios.di

import android.content.Context
import androidx.room.Room
import com.galixo.autoClicker.core.scenarios.data.database.AutoClickerDb
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.MainRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object AutoClickerDatabaseModule {
    @Provides
    @Singleton
    fun providesAutoClickerDatabase(
        @ApplicationContext context: Context,
    ): AutoClickerDb =
        Room.databaseBuilder(
            context.applicationContext,
            AutoClickerDb::class.java,
            "auto_clicker_database"
        ).build()
}

@Module
@InstallIn(SingletonComponent::class)
interface MainModule {

    @Binds
    @Singleton
    fun providesMainRepository(dumbRepository: MainRepository): IMainRepository
}