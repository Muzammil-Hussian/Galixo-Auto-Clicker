package com.galixo.autoClicker.core.scenarios.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Allows to access and edit the  scenario in the database. */
@Dao
interface ScenarioDao {

    /**
     * Get all  scenario and their  actions.
     *
     * @return the Flow on the list of scenarios.
     */
    @Transaction
    @Query("SELECT * FROM scenario_table ORDER BY name ASC")
    fun getScenariosWithActionsFlow(): Flow<List<ScenarioWithActions>>

    /**
     * Get the specified  scenario with its  actions.
     *
     * @return the  scenario if found, null if not.
     */
    @Transaction
    @Query("SELECT * FROM scenario_table WHERE id=:dbId")
    suspend fun getScenariosWithAction(dbId: Long): ScenarioWithActions?

    /**
     * Get the specified  scenario with its  actions.
     *
     * @return the  scenario if found, null if not.
     */
    @Transaction
    @Query("SELECT * FROM scenario_table WHERE id=:dbId")
    fun getScenariosWithActionFlow(dbId: Long): Flow<ScenarioWithActions?>

    /** Get the  actions for a scenario, ordered by their priority. */
    @Query("SELECT * FROM action_table WHERE scenario_id!=:scenarioId")
    fun getAllActionsExcept(scenarioId: Long): Flow<List<ActionEntity>>

    /** Get the  actions for a scenario, ordered by their priority. */
    @Query("SELECT * FROM action_table WHERE scenario_id=:scenarioId ORDER BY priority ASC")
    fun getActions(scenarioId: Long): List<ActionEntity>

    /**
     * Add a new scenario to the database.
     *
     * @param scenario the  scenario to be added.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addScenario(scenario: ScenarioEntity): Long

    /**
     * Update a  scenario to the database.
     *
     * @param scenario the  scenario to be added.
     */
    @Update
    suspend fun updateScenario(scenario: ScenarioEntity)

    /**
     * Delete the provided click scenario from the database.
     *
     * @param scenarioId the identifier of the scenario to be deleted.
     */
    @Query("DELETE FROM scenario_table WHERE id = :scenarioId")
    suspend fun deleteScenario(scenarioId: Long)

    /**
     * Add new dub actions to the database.
     *
     * @param actions the  actions to be added.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addActions(actions: List<ActionEntity>): List<Long>

    /**
     * Update the selected  actions.
     *
     * @param actions the  actions to be updated.
     */
    @Update
    suspend fun updateActions(actions: List<ActionEntity>)

    /**
     * Delete the selected  actions.
     *
     * @param actions the  actions to be deleted.
     */
    @Delete
    suspend fun deleteActions(actions: List<ActionEntity>)

}