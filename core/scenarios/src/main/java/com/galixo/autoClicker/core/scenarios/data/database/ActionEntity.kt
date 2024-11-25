package com.galixo.autoClicker.core.scenarios.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.galixo.autoClicker.core.common.base.interfaces.EntityWithId
import kotlinx.serialization.Serializable

/**
 * Entity defining an action from an action.
 */
@Entity(
    tableName = "action_table",
    indices = [Index("scenario_id")],
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["scenario_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
@Serializable
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) override var id: Long,
    @ColumnInfo(name = "scenario_id") var scenarioId: Long,
    @ColumnInfo(name = "priority") var priority: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: ActionType,

    // Only for repeatable actions
    @ColumnInfo(name = "repeat_count") val repeatCount: Int? = null,
    @ColumnInfo(name = "is_repeat_infinite") val isRepeatInfinite: Boolean? = null,
    @ColumnInfo(name = "repeat_delay") val repeatDelay: Long? = null,

    // ActionType.CLICK
    @ColumnInfo(name = "press_duration") val pressDuration: Long? = null,
    @ColumnInfo(name = "x") val x: Int? = null,
    @ColumnInfo(name = "y") val y: Int? = null,

    // ActionType.SWIPE
    @ColumnInfo(name = "swipe_duration") val swipeDuration: Long? = null,
    @ColumnInfo(name = "fromX") val fromX: Int? = null,
    @ColumnInfo(name = "fromY") val fromY: Int? = null,
    @ColumnInfo(name = "toX") val toX: Int? = null,
    @ColumnInfo(name = "toY") val toY: Int? = null,

    ) : EntityWithId

/**
 * Type of [ActionEntity].
 * For each type there is a set of values that will be available in the database, all others will always be null. Refers
 * to the [ActionEntity] documentation for values/type association.
 *
 * /!\ DO NOT RENAME: ActionType enum name is used in the database.
 */
enum class ActionType {
    /** A single tap on the screen. */
    CLICK,
    /** A swipe on the screen. */
    SWIPE
}

/** Type converter to read/write the [ActionType] into the database. */
internal class ActionTypeStringConverter {
    @TypeConverter
    fun fromString(value: String): ActionType = ActionType.valueOf(value)
    @TypeConverter
    fun toString(action: ActionType): String = action.toString()
}