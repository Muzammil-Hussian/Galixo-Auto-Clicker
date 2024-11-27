package com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class ScenarioConfigViewModel @Inject constructor(
    private val editionRepository: EditionRepository
) : ViewModel() {

    private val userModifications: StateFlow<Scenario?> =
        editionRepository.editedScenario

    val canBeSaved: Flow<Boolean> = userModifications.map { scenario ->
        scenario?.isValid() == true
    }


    /** The number of times to repeat the scenario. */
    val repeatCount: Flow<String> = userModifications
        .filterNotNull()
        .map { it.repeatCount.toString() }
        .take(1)

    /** Tells if the repeat count value is valid or not. */
    val repeatCountError: Flow<Boolean> = userModifications
        .map { it == null || it.repeatCount <= 0 }

    /** Tells if the scenario should be repeated infinitely. */
    val repeatInfiniteState: Flow<Boolean> = userModifications
        .map { it == null || it.isRepeatInfinite }

    /** The maximum duration of the execution in minutes. */
    val maxDurationMin: Flow<String> = userModifications
        .filterNotNull()
        .map { it.maxDurationMin.toString() }
        .take(1)

    /** Tells if the repeat count value is valid or not. */
    val maxDurationMinError: Flow<Boolean> = userModifications
        .map { it == null || it.maxDurationMin <= 0 }

    /** Tells if there is no maximum duration. */
    val maxDurationMinInfiniteState: Flow<Boolean> = userModifications
        .map { it == null || it.isDurationInfinite }

    /** The randomization value for the scenario. */
    val randomization: Flow<Boolean> = userModifications
        .map { it?.randomize == true }

    fun toggleRandomization() {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(scenario.copy(randomize = !scenario.randomize))
        }
    }


    val repeatMode: Flow<String> =
        combine(repeatInfiniteState, maxDurationMin, repeatCount) { isInfinite, duration, count ->
            when {
                isInfinite -> "Infinite"
                duration.isNotEmpty() -> "Stop after $duration minutes"
                count.isNotEmpty() -> "Stop after $count cycles"
                else -> "Not Set"
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, "Not Set")

    fun setMaxDurationMinutes(durationMinutes: Int) {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(
                scenario.copy(
                    maxDurationMin = durationMinutes,
                    isDurationInfinite = false
                )
            )
        }
    }

    fun setRepeatCount(repeatCount: Int) {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(
                scenario.copy(
                    repeatCount = repeatCount,
                    isRepeatInfinite = false // Ensure it's not infinite when cycle count is set
                )
            )
        }
    }

    fun toggleInfiniteRepeat() {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(scenario.copy(isRepeatInfinite = !scenario.isRepeatInfinite))
        }
    }

    fun toggleInfiniteMaxDuration() {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(scenario.copy(isDurationInfinite = !scenario.isDurationInfinite))
        }
    }

}


private const val TAG = "ScenarioConfigViewModelLogs"