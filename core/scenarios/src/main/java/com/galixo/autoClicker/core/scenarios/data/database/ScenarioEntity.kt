package com.galixo.autoClicker.core.scenarios.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.galixo.autoClicker.core.common.base.interfaces.EntityWithId
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import kotlinx.serialization.Serializable

/**
 * Entity defining a scenario.
 *
 * A scenario has a relation "one to many" with [ActionEntity], which is represented
 * by [ScenarioWithActions].
 *
 * @param id the unique identifier for a scenario.
 * @param name the name of the scenario.
 * @param repeatCount
 * @param isRepeatInfinite
 * @param maxDurationMin
 * @param isDurationInfinite
 */
@Entity(tableName = "scenario_table")
@Serializable
data class ScenarioEntity(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "repeat_count") val repeatCount: Int,
    @ColumnInfo(name = "is_repeat_infinite") val isRepeatInfinite: Boolean,
    @ColumnInfo(name = "max_duration_minutes") val maxDurationMin: Int,
    @ColumnInfo(name = "is_duration_infinite") val isDurationInfinite: Boolean,
    @ColumnInfo(name = "randomize") val randomize: Boolean,
    @ColumnInfo(name = "scenario_mode") val scenarioMode: ScenarioMode
) : EntityWithId

/**
 * Entity embedding a scenario and its actions.
 *
 * Automatically do the junction between scenario_table and action_table, and provide
 * this representation of the one to many relation between scenario and action entity.
 *
 * @param scenario the scenario entity.
 * @param actions the list of actions entity for this scenario.
 */
@Serializable
data class ScenarioWithActions(
    @Embedded val scenario: ScenarioEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "scenario_id"
    )
    val actions: List<ActionEntity>
)