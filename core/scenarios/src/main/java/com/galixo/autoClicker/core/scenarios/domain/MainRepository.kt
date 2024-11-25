package com.galixo.autoClicker.core.scenarios.domain

import com.galixo.autoClicker.core.scenarios.data.database.ScenarioDataSource
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import kotlinx.coroutines.flow.Flow

class MainRepository(
    private val scenarioDataSource:ScenarioDataSource
) : IMainRepository {


    override val scenarios: Flow<List<Scenario>> =
        scenarioDataSource.getAllScenarios

    override suspend fun getScenario(dbId: Long): Scenario? =
        scenarioDataSource.getScenario(dbId)

    override fun getScenarioFlow(dbId: Long): Flow<Scenario?> =
        scenarioDataSource.getScenarioFlow(dbId)

    override fun getAllActionsFlowExcept(scenarioDbId: Long): Flow<List<Action>> =
        scenarioDataSource.getAllActionsExcept(scenarioDbId)

    override suspend fun addScenario(scenario: Scenario): Long {
        return scenarioDataSource.addScenario(scenario)
    }

    override suspend fun addScenarioCopy(scenario: ScenarioWithActions): Long? =
        scenarioDataSource.addScenarioCopy(scenario)

    override suspend fun updateScenario(scenario: Scenario) {
        scenarioDataSource.updateScenario(scenario)
    }

    override suspend fun deleteScenario(scenario: Scenario) {
        scenarioDataSource.deleteScenario(scenario)
    }
}