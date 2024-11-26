package com.galixo.autClicker.feature.backup.data.scenario

import android.util.Log
import com.galixo.autClicker.feature.backup.data.base.ScenarioBackupSerializer
import com.galixo.autoClicker.core.common.base.extensions.getBoolean
import com.galixo.autoClicker.core.common.base.extensions.getEnum
import com.galixo.autoClicker.core.common.base.extensions.getInt
import com.galixo.autoClicker.core.common.base.extensions.getJsonArray
import com.galixo.autoClicker.core.common.base.extensions.getJsonObject
import com.galixo.autoClicker.core.common.base.extensions.getLong
import com.galixo.autoClicker.core.common.base.extensions.getString
import com.galixo.autoClicker.core.scenarios.data.database.ActionEntity
import com.galixo.autoClicker.core.scenarios.data.database.ActionType
import com.galixo.autoClicker.core.scenarios.data.database.DATABASE_VERSION
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioEntity
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_COUNT_MAX_VALUE
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_COUNT_MIN_VALUE
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MAX_MS
import com.galixo.autoClicker.core.scenarios.domain.model.REPEAT_DELAY_MIN_MS
import com.galixo.autoClicker.core.scenarios.domain.model.SCENARIO_MAX_DURATION_MINUTES
import com.galixo.autoClicker.core.scenarios.domain.model.SCENARIO_MIN_DURATION_MINUTES
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.json.jsonObject
import java.io.InputStream
import java.io.OutputStream


@OptIn(ExperimentalSerializationApi::class)
internal class ScenarioSerializer : ScenarioBackupSerializer<ScenarioBackup> {

    /**
     * Serialize a scenario.
     *
     * @param scenarioBackup the scenario to serialize.
     * @param outputStream the stream to serialize into.
     */
    override fun serialize(scenarioBackup: ScenarioBackup, outputStream: OutputStream) {
        Json.encodeToStream(scenarioBackup, outputStream)
    }

    /**
     * Deserialize a scenario.
     * Depending of the detected version, either kotlin or compat serialization will be used.
     *
     * @param json the stream to deserialize from.
     *
     * @return the scenario backup deserialized from the json.
     */
    override fun deserialize(json: InputStream): ScenarioBackup? {
        Log.d(TAG, "Deserializing scenario")

        val jsonBackup =
            Json.parseToJsonElement(json.readBytes().toString(Charsets.UTF_8)).jsonObject
        val version = jsonBackup.getInt("version", true) ?: -1

        val scenario = when {
            version < 1 -> {
                Log.w(TAG, "Can't deserialize scenario, invalid version.")
                null
            }

            version == DATABASE_VERSION -> {
                Log.d(TAG, "Current version, use standard serialization.")
                Json.decodeFromJsonElement<ScenarioBackup>(jsonBackup).scenario
            }

            else -> {
                Log.d(TAG, "$version is not the current version, use compat serialization.")
                jsonBackup.deserializeCompleteScenarioCompat()
            }
        }

        if (scenario == null) {
            Log.w(TAG, "Can't deserialize scenario.")
            return null
        }

        return ScenarioBackup(
            version = version,
            screenWidth = jsonBackup.getInt("screenWidth") ?: 0,
            screenHeight = jsonBackup.getInt("screenHeight") ?: 0,
            scenario = scenario,
        )
    }

    private fun JsonObject.deserializeCompleteScenarioCompat(): ScenarioWithActions? {
        val jsonCompleteScenario = getJsonObject("scenario") ?: return null

        val scenario: ScenarioEntity = jsonCompleteScenario.getJsonObject("scenario")
            ?.deserializeScenarioCompat()
            ?: return null

        return ScenarioWithActions(
            scenario = scenario,
            actions = jsonCompleteScenario.getJsonArray("actions")
                ?.deserializeActionsCompat()
                ?: return null
        )
    }

    private fun JsonObject.deserializeScenarioCompat(): ScenarioEntity? {
        val id = getLong("id", true) ?: return null

        val name = getString("name", true)
        if (name.isNullOrEmpty()) return null

        return ScenarioEntity(
            id = id,
            name = name,
            repeatCount = getInt("repeatCount")
                ?.coerceIn(REPEAT_COUNT_MIN_VALUE..REPEAT_COUNT_MAX_VALUE)
                ?: DEFAULT_REPEAT_COUNT,
            isRepeatInfinite = getBoolean("isRepeatInfinite") ?: DEFAULT_REPEAT_IS_INFINITE,
            maxDurationMin = getInt("maxDurationMin")
                ?.coerceIn(SCENARIO_MIN_DURATION_MINUTES..SCENARIO_MAX_DURATION_MINUTES)
                ?: DEFAULT_MAX_DURATION_MINUTES,
            isDurationInfinite = getBoolean("isDurationInfinite")
                ?: DEFAULT_DURATION_IS_INFINITE,
            randomize = getBoolean("randomize") ?: DEFAULT_RANDOMIZE,
            scenarioMode = ScenarioMode.SINGLE_MODE
        )
    }

    private fun JsonArray.deserializeActionsCompat(): List<ActionEntity> =
        mapNotNull { jsonElement ->
            jsonElement.getJsonObject()?.let { jsonAction ->
                when (jsonAction.getEnum<ActionType>("type", true)) {
                    ActionType.CLICK -> jsonAction.deserializeClickCompat()
                    ActionType.SWIPE -> jsonAction.deserializeSwipeCompat()
                    else -> null
                }
            }
        }

    private fun JsonObject.deserializeClickCompat(): ActionEntity? {
        val id = getLong("id", true) ?: return null
        val scenarioId = getLong("scenario_id", true) ?: return null

        val name = getString("name", true)
        if (name.isNullOrEmpty()) return null

        val x = getInt("x", true) ?: return null
        val y = getInt("y", true) ?: return null

        return ActionEntity(
            id = id,
            scenarioId = scenarioId,
            name = name,
            priority = getInt("priority")?.coerceAtLeast(0) ?: 0,
            type = ActionType.CLICK,
            x = x,
            y = y,
            pressDuration = getLong("pressDuration")
                ?.coerceIn(DURATION_LOWER_BOUND..DURATION_GESTURE_UPPER_BOUND)
                ?: DEFAULT_CLICK_DURATION,
            repeatCount = getInt("repeatCount")
                ?.coerceIn(REPEAT_COUNT_MIN_VALUE..REPEAT_COUNT_MAX_VALUE)
                ?: DEFAULT_REPEAT_COUNT,
            isRepeatInfinite = getBoolean("isRepeatInfinite") ?: DEFAULT_REPEAT_IS_INFINITE,
            repeatDelay = getLong("repeatDelay")
                ?.coerceIn(REPEAT_DELAY_MIN_MS..REPEAT_DELAY_MAX_MS)
                ?: DEFAULT_REPEAT_DELAY_MS,
        )
    }

    private fun JsonObject.deserializeSwipeCompat(): ActionEntity? {
        val id = getLong("id", true) ?: return null
        val scenarioId = getLong("scenario_id", true) ?: return null

        val name = getString("name", true)
        if (name.isNullOrEmpty()) return null

        val fromX = getInt("fromX", true) ?: return null
        val fromY = getInt("fromY", true) ?: return null
        val toX = getInt("toX", true) ?: return null
        val toY = getInt("toY", true) ?: return null

        return ActionEntity(
            id = id,
            scenarioId = scenarioId,
            name = name,
            priority = getInt("priority")?.coerceAtLeast(0) ?: 0,
            type = ActionType.SWIPE,
            fromX = fromX,
            fromY = fromY,
            toX = toX,
            toY = toY,
            swipeDuration = getLong("swipeDuration")
                ?.coerceIn(DURATION_LOWER_BOUND..DURATION_GESTURE_UPPER_BOUND)
                ?: DEFAULT_SWIPE_DURATION,
            repeatCount = getInt("repeatCount")
                ?.coerceIn(REPEAT_COUNT_MIN_VALUE..REPEAT_COUNT_MAX_VALUE)
                ?: DEFAULT_REPEAT_COUNT,
            isRepeatInfinite = getBoolean("isRepeatInfinite") ?: DEFAULT_REPEAT_IS_INFINITE,
            repeatDelay = getLong("repeatDelay")
                ?.coerceIn(REPEAT_DELAY_MIN_MS..REPEAT_DELAY_MAX_MS)
                ?: DEFAULT_REPEAT_DELAY_MS,
        )
    }

}

/** The minimum value for all durations. */
private const val DURATION_LOWER_BOUND = 1L

/** The maximum value for all gestures durations. */
private const val DURATION_GESTURE_UPPER_BOUND = 59_999L

/** Default click duration in ms on compat deserialization. */
private const val DEFAULT_CLICK_DURATION = 1L

/** Default swipe duration in ms on compat deserialization. */
private const val DEFAULT_SWIPE_DURATION = 250L


private const val DEFAULT_REPEAT_COUNT = 1
private const val DEFAULT_REPEAT_IS_INFINITE = false
private const val DEFAULT_REPEAT_DELAY_MS = 1L
private const val DEFAULT_MAX_DURATION_MINUTES = 1
private const val DEFAULT_DURATION_IS_INFINITE = true
private const val DEFAULT_RANDOMIZE = true

private const val TAG = "ScenarioSerializer"