package com.galixo.autoClicker.modules.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.findAppropriateTimeUnit
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.formatDuration
import com.galixo.autoClicker.feature.config.data.getConfigPreferences
import com.galixo.autoClicker.feature.config.data.putSwipeDurationConfig
import com.galixo.autoClicker.feature.config.data.putSwipeRepeatDelayConfig
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeDurationMs
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeRepeatDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _intervalValueMs = MutableStateFlow(context.getDefaultSwipeRepeatDelay())
    private val _intervalTimeUnit = MutableStateFlow(_intervalValueMs.value.findAppropriateTimeUnit())

    private val _swipeDurationValueMs = MutableStateFlow(context.getDefaultSwipeDurationMs())
    private val _swipeDurationTimeUnit = MutableStateFlow(_swipeDurationValueMs.value.findAppropriateTimeUnit())

    val intervalValue: Flow<String> = combine(_intervalValueMs, _intervalTimeUnit) { valueMs, unit ->
        unit.formatDuration(valueMs)
    }

    val swipeDurationValue: Flow<String> = combine(_swipeDurationValueMs, _swipeDurationTimeUnit) { valueMs, unit ->
        unit.formatDuration(valueMs)
    }

    val intervalTimeUnit: StateFlow<TimeUnitDropDownItem> = _intervalTimeUnit
    val swipeDurationTimeUnit: StateFlow<TimeUnitDropDownItem> = _swipeDurationTimeUnit

    val intervalError: Flow<Boolean> = _intervalValueMs.combine(_intervalTimeUnit) { valueMs, unit ->
        validateInput(valueMs, unit)
    }

    val swipeDurationError: Flow<Boolean> = _swipeDurationValueMs.combine(_swipeDurationTimeUnit) { valueMs, unit ->
        validateInput(valueMs, unit)
    }

    fun setIntervalValue(value: Long) {
        _intervalValueMs.value = value
        saveIntervalValue(value)
    }

    fun setIntervalTimeUnit(unit: TimeUnitDropDownItem) {
        _intervalTimeUnit.value = unit
    }

    fun setSwipeDurationValue(value: Long) {
        _swipeDurationValueMs.value = value
        saveSwipeDurationValue(value)
    }

    fun setSwipeDurationTimeUnit(unit: TimeUnitDropDownItem) {
        _swipeDurationTimeUnit.value = unit
    }

    private fun validateInput(valueMs: Long, unit: TimeUnitDropDownItem): Boolean {
        return when (unit) {
            TimeUnitDropDownItem.Milliseconds -> valueMs >= 40
            TimeUnitDropDownItem.Seconds -> valueMs >= 1.seconds.inWholeMilliseconds
            TimeUnitDropDownItem.Minutes -> valueMs >= 1.minutes.inWholeMilliseconds
            TimeUnitDropDownItem.Hours -> valueMs >= 1.hours.inWholeMilliseconds
        }
    }

    private fun saveIntervalValue(value: Long) {
        Log.i(TAG, "saveIntervalValue: $value")
        context.getConfigPreferences().edit().putSwipeRepeatDelayConfig(value).apply()
    }

    private fun saveSwipeDurationValue(value: Long) {
        context.getConfigPreferences().edit().putSwipeDurationConfig(value).apply()
    }
}

private const val TAG = "SettingViewModel"