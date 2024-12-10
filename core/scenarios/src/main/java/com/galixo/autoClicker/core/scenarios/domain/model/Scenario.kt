package com.galixo.autoClicker.core.scenarios.domain.model

import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.common.base.interfaces.Identifiable
import com.galixo.autoClicker.core.common.ui.enum.RepeatMode

data class Scenario(
    override val id: Identifier,
    var name: String,
    val actions: List<Action> = emptyList(),
    override val repeatCount: Int,
    override val isRepeatInfinite: Boolean,
    val maxDurationSec: Int,
    val isDurationInfinite: Boolean,
    val randomize: Boolean,

    val scenarioMode: ScenarioMode
) : Identifiable, Repeatable {

    fun getRepeatMode(): RepeatMode {
        return when {
            isDurationInfinite && isRepeatInfinite -> RepeatMode.NEVER_STOP
            isDurationInfinite && !isRepeatInfinite -> RepeatMode.STOP_AFTER_REPEATS_COUNT
            !isDurationInfinite && isRepeatInfinite -> RepeatMode.STOP_AFTER_DURATION
            else -> RepeatMode.NEVER_STOP
        }
    }

    fun isValid(): Boolean = name.isNotEmpty() && actions.isNotEmpty()
}

enum class ScenarioMode { SINGLE_MODE, MULTI_MODE }

const val SCENARIO_MIN_DURATION_MINUTES = 1
const val SCENARIO_MAX_DURATION_MINUTES = 1440