package com.galixo.autoClicker.core.common.overlays.di

import com.galixo.autoClicker.core.common.overlays.manager.OverlayManager
import com.galixo.autoClicker.core.common.overlays.menu.common.OverlayMenuPositionDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface OverlaysEntryPoint {

    fun overlayManager(): OverlayManager
    fun overlayMenuPositionDataSource(): OverlayMenuPositionDataSource
}