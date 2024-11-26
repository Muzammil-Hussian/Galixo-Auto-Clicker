package com.galixo.autoClicker.modules.script.presentation.ui.listener

import com.galixo.autoClicker.core.scenarios.domain.model.Scenario

interface Listener {
    fun startScenario(item: Scenario)
}

