package com.galixo.autoClicker.core.common.display.di

import com.galixo.autoClicker.core.common.display.DisplayConfigManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface DisplayEntryPoint {
    fun displayMetrics(): DisplayConfigManager
}