package com.galixo.autoClicker.localService

import com.galixo.autoClicker.core.scenarios.domain.model.Scenario

interface ILocalService {
    fun startScenario(scenario: Scenario)
    fun stop()
    fun release()
}