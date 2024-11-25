package com.galixo.autoClicker.core.scenarios.data.database

import android.util.Log
import com.galixo.autoClicker.core.common.base.DatabaseListUpdater
import com.galixo.autoClicker.core.common.base.extensions.mapList
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.domain.model.toDomain
import com.galixo.autoClicker.core.scenarios.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScenarioDataSource @Inject constructor(
    database: AutoClickerDb,
) {

    private val scenarioDao: ScenarioDao = database.scenarioDao()

    /** Updater for a list of actions. */
    private val actionsUpdater = DatabaseListUpdater<Action, ActionEntity>()

    val getAllScenarios: Flow<List<Scenario>> =
        scenarioDao.getScenariosWithActionsFlow()
            .mapList { it.toDomain() }

    suspend fun getScenario(dbId: Long): Scenario? =
        scenarioDao.getScenariosWithAction(dbId)
            ?.toDomain()

    fun getScenarioFlow(dbId: Long): Flow<Scenario?> =
        scenarioDao.getScenariosWithActionFlow(dbId)
            .map { it?.toDomain() }

    fun getAllActionsExcept(scenarioDbId: Long): Flow<List<Action>> =
        scenarioDao.getAllActionsExcept(scenarioDbId)
            .mapList { it.toDomain() }

    suspend fun addScenario(scenario: Scenario): Long {
        Log.d(TAG, "Add scenario $scenario")

        val scenarioDbId = scenarioDao.addScenario(scenario.toEntity())

        updateScenarioActions(scenarioDbId, scenario.actions)

        return scenarioDbId
    }

    suspend fun addScenarioCopy(scenarioWithActions: ScenarioWithActions): Long? {
        Log.d(TAG, "Add scenario to copy ${scenarioWithActions.scenario}")

        return try {
            val scenarioId = scenarioDao.addScenario(
                scenarioWithActions.scenario.copy(id = DATABASE_ID_INSERTION)
            )

            scenarioDao.addActions(
                scenarioWithActions.actions.map { actionEntity ->
                    actionEntity.copy(
                        id = DATABASE_ID_INSERTION,
                        scenarioId = scenarioId,
                    )
                }
            )

            scenarioId
        } catch (ex: Exception) {
            Log.e(TAG, "Error while inserting scenario copy", ex)
            null
        }
    }

    suspend fun updateScenario(scenario: Scenario) {
        Log.d(TAG, "Update scenario $scenario")
        val scenarioEntity = scenario.toEntity()

        scenarioDao.updateScenario(scenarioEntity)
        updateScenarioActions(scenarioEntity.id, scenario.actions)
    }

    private suspend fun updateScenarioActions(scenarioDbId: Long, actions: List<Action>) {
        val updater = DatabaseListUpdater<Action, ActionEntity>()
        updater.refreshUpdateValues(
            currentEntities = scenarioDao.getActions(scenarioDbId),
            newItems = actions,
            mappingClosure = { action -> action.toEntity(scenarioDbId = scenarioDbId) }
        )

        Log.d(TAG, " actions updater: $actionsUpdater")

        updater.executeUpdate(
            addList = scenarioDao::addActions,
            updateList = scenarioDao::updateActions,
            removeList = scenarioDao::deleteActions,
        )
    }

    suspend fun deleteScenario(scenario: Scenario) {
        Log.d(TAG, "Delete scenario $scenario")

        scenarioDao.deleteScenario(scenario.id.databaseId)
    }
}

private const val TAG = "ScenarioDataSource"