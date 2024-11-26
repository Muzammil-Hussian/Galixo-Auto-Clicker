package com.galixo.autoClicker.feature.config.ui.actions.click

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

class ClickPointViewModel @Inject constructor() : ViewModel() {

    private val _editedClick: MutableStateFlow<Action.Click?> = MutableStateFlow(null)
    private val editedClick: Flow<Action.Click> = _editedClick.filterNotNull()

    private val _selectedUnitItem: MutableStateFlow<TimeUnitDropDownItem> = MutableStateFlow(
        TimeUnitDropDownItem.Milliseconds)
    val selectedUnitItem: Flow<TimeUnitDropDownItem> = _selectedUnitItem

    @OptIn(ExperimentalCoroutinesApi::class)
    val repeatDelay: Flow<String> = selectedUnitItem
        .flatMapLatest { unitItem ->
            editedClick
                .map { unitItem.formatDuration(it.repeatDelayMs) }
                .take(1)
        }

    fun setEditedClick(click: Action.Click) {
        _selectedUnitItem.value = click.repeatDelayMs.findAppropriateTimeUnit()
        _editedClick.value = click.copy()
    }

    fun getEditedClick(): Action.Click? = _editedClick.value

    fun setRepeatDelay(delayMs: Long) {
        _editedClick.value = _editedClick.value?.let { oldValue ->
            val newDelayMs = delayMs.toDurationMs(_selectedUnitItem.value)
            if (oldValue.repeatDelayMs == newDelayMs) return
            oldValue.copy(repeatDelayMs = delayMs)
        }
    }

    fun getRepeatDelayMs() = _editedClick.value?.repeatDelayMs ?: 0L

    fun setTimeUnit(unit: TimeUnitDropDownItem) { _selectedUnitItem.value = unit }

    fun getTimeUnit(): TimeUnitDropDownItem = _selectedUnitItem.value
}
