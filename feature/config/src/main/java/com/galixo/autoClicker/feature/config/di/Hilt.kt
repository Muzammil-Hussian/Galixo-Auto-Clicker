package com.galixo.autoClicker.feature.config.di

import com.galixo.autoClicker.core.common.overlays.di.OverlayComponent
import com.galixo.autoClicker.feature.config.ui.MainMenuViewModel
import com.galixo.autoClicker.feature.config.ui.actions.click.ClickPointViewModel
import com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.SaveLoadViewModel
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ScenarioConfigViewModel
import com.galixo.autoClicker.feature.config.ui.actions.swipe.SwipePointViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn

@EntryPoint
@InstallIn(OverlayComponent::class)
interface ConfigViewModelsEntryPoint {
    fun mainMenuViewModel(): MainMenuViewModel
    fun clickPointViewModel(): ClickPointViewModel
    fun swipePointViewModel(): SwipePointViewModel
    fun saveLoadViewModel(): SaveLoadViewModel
    fun scenarioConfigViewModel(): ScenarioConfigViewModel
}