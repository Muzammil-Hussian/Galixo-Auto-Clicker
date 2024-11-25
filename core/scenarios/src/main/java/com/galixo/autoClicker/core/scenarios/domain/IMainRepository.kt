package com.galixo.autoClicker.core.scenarios.domain

import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import kotlinx.coroutines.flow.Flow

interface IMainRepository {

    val scenarios: Flow<List<Scenario>>

    suspend fun getScenario(dbId: Long): Scenario?

    fun getScenarioFlow(dbId: Long): Flow<Scenario?>

    fun getAllActionsFlowExcept(scenarioDbId: Long): Flow<List<Action>>

    suspend fun addScenario(scenario: Scenario):Long

    suspend fun addScenarioCopy(scenario: ScenarioWithActions): Long?

    suspend fun updateScenario(scenario: Scenario)

    suspend fun deleteScenario(scenario: Scenario)
}