package com.galixo.autoClicker.feature.config.domain

import android.content.Context
import android.graphics.Point
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.common.base.identifier.IdentifierCreator
import com.galixo.autoClicker.core.scenarios.domain.model.Action

class EditedActionsBuilder {

    private val actionsIdCreator = IdentifierCreator()

    private var scenarioId: Identifier? = null

    internal fun startEdition(scenarioId: Identifier) {
        actionsIdCreator.resetIdCount()
        this.scenarioId = scenarioId
    }

    internal fun clearState() {
        actionsIdCreator.resetIdCount()
        scenarioId = null
    }

    fun createNewClick(context: Context, position: Point, id: Identifier? = null): Action.Click =
        Action.Click(
            id = id ?: actionsIdCreator.generateNewIdentifier(),
            scenarioId = getEditedScenarioIdOrThrow(),
            name = context.getDefaultClickName(),
            position = position,
            pressDurationMs = context.getDefaultClickDurationMs(),
            repeatCount = context.getDefaultClickRepeatCount(),
            isRepeatInfinite = false,
            repeatDelayMs = context.getDefaultClickRepeatDelay(),
        )

    fun createNewSwipe(context: Context, from: Point, to: Point, id: Identifier? = null): Action.Swipe =
        Action.Swipe(
            id = id ?: actionsIdCreator.generateNewIdentifier(),
            scenarioId = getEditedScenarioIdOrThrow(),
            name = context.getDefaultSwipeName(),
            fromPosition = from,
            toPosition = to,
            swipeDurationMs = context.getDefaultSwipeDurationMs(),
            repeatCount = context.getDefaultSwipeRepeatCount(),
            isRepeatInfinite = false,
            repeatDelayMs = context.getDefaultSwipeRepeatDelay(),
        )

    private fun getEditedScenarioIdOrThrow(): Identifier = scenarioId
        ?: throw IllegalStateException("Can't create items without an edited scenario")
}
