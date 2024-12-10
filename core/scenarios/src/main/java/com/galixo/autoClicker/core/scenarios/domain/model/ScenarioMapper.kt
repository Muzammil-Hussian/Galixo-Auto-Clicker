package com.galixo.autoClicker.core.scenarios.domain.model

import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioEntity
import com.galixo.autoClicker.core.scenarios.data.database.ScenarioWithActions

internal fun ScenarioWithActions.toDomain(asDomain: Boolean = false): Scenario =
    Scenario(
        id = Identifier(id = scenario.id, asTemporary = asDomain),
        name = scenario.name,
        repeatCount = scenario.repeatCount,
        isRepeatInfinite = scenario.isRepeatInfinite,
        maxDurationSec = scenario.maxDurationMin,
        isDurationInfinite = scenario.isDurationInfinite,
        randomize = scenario.randomize,
        actions = actions
            .sortedBy { it.priority }
            .map { action -> action.toDomain(asDomain) },
        scenarioMode = scenario.scenarioMode
    )

internal fun Scenario.toEntity(): ScenarioEntity =
    ScenarioEntity(
        id = id.databaseId,
        name = name,
        repeatCount = repeatCount,
        isRepeatInfinite = isRepeatInfinite,
        maxDurationMin = maxDurationSec,
        isDurationInfinite = isDurationInfinite,
        randomize = randomize,
        scenarioMode = scenarioMode
    )