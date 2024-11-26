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

    private val _selectedUnitItem: MutableStateFlow<TimeUnitDropDownItem> = MutableStateFlow(TimeUnitDropDownItem.Milliseconds)
    val selectedUnitItem: Flow<TimeUnitDropDownItem> = _selectedUnitItem


    /** The duration between the press and release of the swipe in milliseconds. */
    val swipeDuration: Flow<String> = editedSwipe
        .map { it.swipeDurationMs.toString() }
        .take(1)

    /** Tells if the press duration value is valid or not. */
    val swipeDurationError: Flow<Boolean> = editedSwipe
        .map { it.swipeDurationMs <= 0 }


    @OptIn(ExperimentalCoroutinesApi::class)
    val repeatDelay: Flow<String> = selectedUnitItem
        .flatMapLatest { unitItem ->
            editedSwipe
                .map { unitItem.formatDuration(it.repeatDelayMs) }
                .take(1)
        }


    fun setEditedSwipe(swipe: Action.Swipe) {
        _selectedUnitItem.value = swipe.repeatDelayMs.findAppropriateTimeUnit()
        _editedSwipe.value = swipe.copy()
    }

    fun getEditedSwipe(): Action.Swipe? = _editedSwipe.value


    fun setPressDurationMs(durationMs: Long) {
        _editedSwipe.value = _editedSwipe.value?.copy(swipeDurationMs = durationMs)
    }

    fun setRepeatDelay(delayMs: Long) {
        _editedSwipe.value = _editedSwipe.value?.let { oldValue ->
            val newDelayMs = delayMs.toDurationMs(_selectedUnitItem.value)
            if (oldValue.repeatDelayMs == newDelayMs) return
            oldValue.copy(repeatDelayMs = delayMs)
        }
    }

    fun getRepeatDelayMs() = _editedSwipe.value?.repeatDelayMs ?: 0L

    fun setTimeUnit(unit: TimeUnitDropDownItem) { _selectedUnitItem.value = unit }

    fun getTimeUnit(): TimeUnitDropDownItem = _selectedUnitItem.value
}