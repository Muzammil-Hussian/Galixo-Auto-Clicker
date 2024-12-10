package com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig

import android.content.Context
import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.TimeUnitDropDownItem
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.findAppropriateTimeUnit
import com.galixo.autoClicker.core.common.ui.bindings.dropdown.formatDuration
import com.galixo.autoClicker.core.common.ui.enum.RepeatMode
import com.galixo.autoClicker.core.common.ui.model.RepeatModeState
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.data.getConfigPreferences
import com.galixo.autoClicker.feature.config.data.putClickRepeatDelayConfig
import com.galixo.autoClicker.feature.config.data.putRandomization
import com.galixo.autoClicker.feature.config.data.putSwipeDurationConfig
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import com.galixo.autoClicker.feature.config.domain.getDefaultClickRepeatDelay
import com.galixo.autoClicker.feature.config.domain.getDefaultRandomize
import com.galixo.autoClicker.feature.config.domain.getDefaultSwipeDurationMs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ScenarioConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context, internal val editionRepository: EditionRepository
) : ViewModel() {

    val repeatMode: Flow<String> = editionRepository.editedScenario.map {
        when (it?.getRepeatMode()) {
            RepeatMode.NEVER_STOP -> context.getString(R.string.never_stop)
            RepeatMode.STOP_AFTER_DURATION -> {
                val hours = it.maxDurationSec / 3600
                val minutes = (it.maxDurationSec % 3600) / 60
                val seconds = it.maxDurationSec % 60
                "${hours}h ${minutes}min ${seconds}sec"
            }

            RepeatMode.STOP_AFTER_REPEATS_COUNT -> "${it.repeatCount} reps"
            else -> context.getString(R.string.never_stop)
        }
    }

    private val _state = MutableStateFlow(getInitialState())
    val state: StateFlow<RepeatModeState> = _state

    private fun getInitialState(): RepeatModeState {
        val scenario = editionRepository.editedScenario.value
        val repeatMode = scenario?.getRepeatMode() ?: RepeatMode.NEVER_STOP

        val durationSec = scenario?.maxDurationSec ?: 30

        val hours = durationSec / 3600
        val minutes = (durationSec % 3600) / 60
        val seconds = durationSec % 60
        val repeatCount = scenario?.repeatCount ?: 10

        return RepeatModeState(
            selectedMode = repeatMode,
            hours = hours,
            minutes = minutes,
            seconds = seconds, repeatCount = repeatCount
        )
    }

    fun updateScenario(updatedScenario: Scenario?) {
        updatedScenario?.let {
            val newRepeatMode = _state.value.selectedMode

            val newScenario = when (newRepeatMode) {
                RepeatMode.STOP_AFTER_DURATION -> it.copy(maxDurationSec = it.maxDurationSec, isDurationInfinite = false, isRepeatInfinite = true)
                RepeatMode.STOP_AFTER_REPEATS_COUNT -> it.copy(isDurationInfinite = true, repeatCount = _state.value.repeatCount, isRepeatInfinite = false)
                RepeatMode.NEVER_STOP -> it.copy(isDurationInfinite = true, isRepeatInfinite = true)
            }

            editionRepository.updateScenario(newScenario)
        }
    }

    fun updateRepeatMode(mode: RepeatMode) {
        _state.value = _state.value.copy(selectedMode = mode)
    }

    fun updateTimeDuration(hours: Int, minutes: Int, seconds: Int) { _state.value = _state.value.copy(hours = hours, minutes = minutes, seconds = seconds)
    }

    fun updateRepeats(repeats: Int) {
        _state.value = _state.value.copy(repeatCount = repeats)
    }


    private val _intervalValueMs = MutableStateFlow(context.getDefaultClickRepeatDelay())
    val intervalValue get() = _intervalValueMs

    private val _selectedIntervalTimeUnit = MutableStateFlow(_intervalValueMs.value.findAppropriateTimeUnit())
    val selectedIntervalTimeUnit = _selectedIntervalTimeUnit

    @OptIn(ExperimentalCoroutinesApi::class)
    val intervalDuration: Flow<String> = selectedIntervalTimeUnit.flatMapLatest { timeUnit ->
        intervalValue.map { timeUnit.formatDuration(it) }.take(1)
    }

    fun setIntervalValue(value: Long) {
        _intervalValueMs.value = value
    }

    fun setSelectedIntervalTimeUnit(unit: TimeUnitDropDownItem) {
        _selectedIntervalTimeUnit.value = unit
    }

    fun getSelectedIntervalUnitTime(): TimeUnitDropDownItem = _selectedIntervalTimeUnit.value

    fun saveIntervalValue(value: Long) = context.getConfigPreferences().edit().putClickRepeatDelayConfig(value).apply()

    private val _swipeDurationValueMs = MutableStateFlow(context.getDefaultSwipeDurationMs())
    val swipeDurationValue get() = _swipeDurationValueMs

    private val _selectedSwipeDurationTimeUnit = MutableStateFlow(_swipeDurationValueMs.value.findAppropriateTimeUnit())
    val selectedSwipeDurationTimeUnit get() = _selectedSwipeDurationTimeUnit

    val isValid = combine(intervalValue, swipeDurationValue) { interval, duration -> interval >= 100 && duration >= 350 }

    @OptIn(ExperimentalCoroutinesApi::class)
    val swipeDuration: Flow<String> = selectedSwipeDurationTimeUnit.flatMapLatest { timeUnit ->
        swipeDurationValue.map { timeUnit.formatDuration(it) }.take(1)
    }

    fun setSwipeDurationValue(value: Long) {
        _swipeDurationValueMs.value = value
    }

    fun setSwipeDurationTimeUnit(unit: TimeUnitDropDownItem) {
        _selectedSwipeDurationTimeUnit.value = unit
    }

    fun getSelectedSwipeDurationTimeUnit() = _selectedSwipeDurationTimeUnit.value

    internal fun saveSwipeDurationValue(value: Long) = context.getConfigPreferences().edit().putSwipeDurationConfig(value).apply()

    internal fun validateInput(isSwipeDuration: Boolean, valueMs: Long, unit: TimeUnitDropDownItem): Boolean {
        return when (unit) {
            TimeUnitDropDownItem.Milliseconds -> valueMs >= if (isSwipeDuration) 350 else 40
            TimeUnitDropDownItem.Seconds -> valueMs >= 1.seconds.inWholeMilliseconds
            TimeUnitDropDownItem.Minutes -> valueMs >= 1.minutes.inWholeMilliseconds
            TimeUnitDropDownItem.Hours -> valueMs >= 1.hours.inWholeMilliseconds
        }
    }

    fun setRandomization(randomize: Boolean) = context.getConfigPreferences().edit().putRandomization(randomize).apply()
    fun getRandomization() = context.getDefaultRandomize()
}

private const val TAG = "ScenarioConfigViewModel"