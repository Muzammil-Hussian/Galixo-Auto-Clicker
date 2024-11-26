package com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.callback

import android.view.MenuItem
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario

interface ScriptItemCallback {
    fun onItemMenuClick(menuItem: MenuItem, item: Scenario)
    fun onStartScenario(item: Scenario)
}