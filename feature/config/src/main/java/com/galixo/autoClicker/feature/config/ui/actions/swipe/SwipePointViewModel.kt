package com.galixo.autoClicker.feature.config.ui.actions.swipe

import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.findAppropriateTimeUnit
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.formatDuration
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.toDurationMs
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject


class SwipePointViewModel @Inject constructor() : ViewModel() {

    private val _editedSwipe: MutableStateFlow<Action.Swipe?> = MutableStateFlow(null)
    private val editedSwipe: Flow<Action.Swipe> = _editedSwipe.filterNotNull()


    // Separate time unit state for interval and swipe duration
    private val _intervalUnit: MutableStateFlow<TimeUnitDropDownItem> = MutableStateFlow(TimeUnitDropDownItem.Milliseconds)
    val intervalUnit: Flow<TimeUnitDropDownItem> = _intervalUnit

    private val _swipeDurationUnit: MutableStateFlow<TimeUnitDropDownItem> = MutableStateFlow(TimeUnitDropDownItem.Milliseconds)
    val swipeDurationUnit: Flow<TimeUnitDropDownItem> = _swipeDurationUnit


    @OptIn(ExperimentalCoroutinesApi::class)
    val repeatDelay: Flow<String> = intervalUnit
        .flatMapLatest { unitItem ->
            editedSwipe
                .map { unitItem.formatDuration(it.repeatDelayMs) }
                .take(1)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val swipeDuration: Flow<String> = swipeDurationUnit
        .flatMapLatest { unitItem ->
            editedSwipe
                .map { unitItem.formatDuration(it.swipeDurationMs) }
                .take(1)
        }

    fun setEditedSwipe(swipe: Action.Swipe) {

        _intervalUnit.value = swipe.repeatDelayMs.findAppropriateTimeUnit()
        _swipeDurationUnit.value = swipe.swipeDurationMs.findAppropriateTimeUnit()
        _editedSwipe.value = swipe.copy()

        _editedSwipe.value = swipe.copy()
    }

    fun getEditedSwipe(): Action.Swipe? = _editedSwipe.value


    fun setPressDurationMs(durationMs: Long) {
        _editedSwipe.value = _editedSwipe.value?.copy(swipeDurationMs = durationMs)
    }

    fun getPressDurationMs() = _editedSwipe.value?.swipeDurationMs ?: 0L


    fun setRepeatDelay(delayMs: Long) {
        _editedSwipe.value = _editedSwipe.value?.let { oldValue ->
            val newDelayMs = delayMs.toDurationMs(_intervalUnit.value)
            if (oldValue.repeatDelayMs == newDelayMs) return
            oldValue.copy(repeatDelayMs = delayMs)
        }
    }

    fun getRepeatDelayMs() = _editedSwipe.value?.repeatDelayMs ?: 0L


    fun setIntervalUnit(unit: TimeUnitDropDownItem) {
        _intervalUnit.value = unit
    }

    fun getIntervalUnit(): TimeUnitDropDownItem = _intervalUnit.value


    fun setSwipeDurationUnit(unit: TimeUnitDropDownItem) {
        _swipeDurationUnit.value = unit
    }

    fun getSwipeDurationUnit(): TimeUnitDropDownItem = _swipeDurationUnit.value

}