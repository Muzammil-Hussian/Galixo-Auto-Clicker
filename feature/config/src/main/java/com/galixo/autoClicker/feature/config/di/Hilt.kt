package com.galixo.autoClicker.feature.config.di

import com.galixo.autoClicker.core.common.overlays.di.OverlayComponent
import com.galixo.autoClicker.feature.config.ui.MainMenuViewModel
import com.galixo.autoClicker.feature.config.ui.actions.click.ClickPointViewModel
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.ScenarioSavingLoadingViewModel
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save.SaveActionContentViewModel
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
    fun scenarioSavingLoadingViewModel(): ScenarioSavingLoadingViewModel
    fun scenarioConfigViewModel(): ScenarioConfigViewModel
    fun saveActionContentViewModel(): SaveActionContentViewModel
}