package com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig

import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
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

    /** The event name value currently edited by the user. */
    val scenarioName: Flow<String> = userModifications
        .filterNotNull()
        .map { it.name }
        .take(1)

    /** Tells if the scenario name is valid or not. */
    val scenarioNameError: Flow<Boolean> = userModifications
        .map { it?.name?.isEmpty() == true }

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

    fun setScenarioName(name: String) {
        userModifications.value?.copy(name = name)?.let {
            editionRepository.updateScenario(it)
        }
    }

    fun setRepeatCount(repeatCount: Int) {
        userModifications.value?.copy(repeatCount = repeatCount)?.let {
            editionRepository.updateScenario(it)
        }
    }

    fun toggleInfiniteRepeat() {
        val currentValue = userModifications.value?.isRepeatInfinite ?: return
        userModifications.value?.copy(isRepeatInfinite = !currentValue)?.let {
            editionRepository.updateScenario(it)
        }
    }

    fun setMaxDurationMinutes(durationMinutes: Int) {
        userModifications.value?.copy(maxDurationMin = durationMinutes)?.let {
            editionRepository.updateScenario(it)
        }
    }

    fun toggleInfiniteMaxDuration() {
        val currentValue = userModifications.value?.isDurationInfinite ?: return
        userModifications.value?.copy(isDurationInfinite = !currentValue)?.let {
            editionRepository.updateScenario(it)
        }
    }

    fun toggleRandomization() {
        userModifications.value?.let { scenario ->
            editionRepository.updateScenario(scenario.copy(randomize = !scenario.randomize))
        }
    }
}

private const val TAG = "ScenarioConfigViewModelLogs"