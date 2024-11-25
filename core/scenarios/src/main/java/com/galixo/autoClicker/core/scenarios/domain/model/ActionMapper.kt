package com.galixo.autoClicker.core.scenarios.domain.model

import android.graphics.Point
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.scenarios.data.database.ActionEntity
import com.galixo.autoClicker.core.scenarios.data.database.ActionType

internal fun ActionEntity.toDomain(asDomain: Boolean = false): Action = when (type) {
    ActionType.CLICK -> toDomainClick(asDomain)
    ActionType.SWIPE -> toDomainSwipe(asDomain)
}
internal fun Action.toEntity(scenarioDbId: Long = DATABASE_ID_INSERTION): ActionEntity = when (this) {
    is Action.Click -> toClickEntity(scenarioDbId)
    is Action.Swipe -> toSwipeEntity(scenarioDbId)
}

private fun ActionEntity.toDomainClick(asDomain: Boolean): Action.Click =
    Action.Click(
        id = Identifier(id = id, asTemporary = asDomain),
        scenarioId = Identifier(id = scenarioId, asTemporary = asDomain),
        name = name,
        priority = priority,
        position = Point(x!!, y!!),
        pressDurationMs = pressDuration!!,
        repeatCount = repeatCount!!,
        isRepeatInfinite = isRepeatInfinite!!,
        repeatDelayMs = repeatDelay!!,
    )

private fun ActionEntity.toDomainSwipe(asDomain: Boolean): Action.Swipe =
    Action.Swipe(
        id = Identifier(id = id, asTemporary = asDomain),
        scenarioId = Identifier(id = scenarioId, asTemporary = asDomain),
        name = name,
        priority = priority,
        fromPosition = Point(fromX!!, fromY!!),
        toPosition = Point(toX!!, toY!!),
        swipeDurationMs = swipeDuration!!,
        repeatCount = repeatCount!!,
        isRepeatInfinite = isRepeatInfinite!!,
        repeatDelayMs = repeatDelay!!,
    )


private fun Action.Click.toClickEntity(scenarioDbId: Long): ActionEntity {
    if (!isValid()) throw IllegalStateException("Can't transform to entity, Click is incomplete.")

    return ActionEntity(
        id = id.databaseId,
        scenarioId = if (scenarioDbId != DATABASE_ID_INSERTION) scenarioDbId else scenarioId.databaseId,
        name = name,
        priority = priority,
        type = ActionType.CLICK,
        repeatCount = repeatCount,
        isRepeatInfinite = isRepeatInfinite,
        repeatDelay = repeatDelayMs,
        pressDuration = pressDurationMs,
        x = position.x,
        y = position.y,
    )
}

private fun Action.Swipe.toSwipeEntity(scenarioDbId: Long): ActionEntity {
    if (!isValid()) throw IllegalStateException("Can't transform to entity, Swipe is incomplete.")

    return ActionEntity(
        id = id.databaseId,
        scenarioId = if (scenarioDbId != DATABASE_ID_INSERTION) scenarioDbId else scenarioId.databaseId,
        name = name,
        priority = priority,
        type = ActionType.SWIPE,
        repeatCount = repeatCount,
        isRepeatInfinite = isRepeatInfinite,
        repeatDelay = repeatDelayMs,
        swipeDuration = swipeDurationMs,
        fromX = fromPosition.x,
        fromY = fromPosition.y,
        toX = toPosition.x,
        toY = toPosition.y,
    )
}

