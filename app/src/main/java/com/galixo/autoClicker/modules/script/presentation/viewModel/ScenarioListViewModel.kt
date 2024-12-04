package com.galixo.autoClicker.modules.script.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenarioListViewModel @Inject constructor(private val mainRepository: IMainRepository) : ViewModel() {
    /** Only scenario */
    val allScenarios: Flow<List<Scenario>> = mainRepository.scenarios

    fun onDuplicationScenario(scenario: Scenario) = viewModelScope.launch(Dispatchers.IO) {
        val newScenario = Scenario(
            id = Identifier(databaseId = DATABASE_ID_INSERTION, tempId = 0L),
            name = scenario.name,
            actions = scenario.actions,
            repeatCount = scenario.repeatCount,
            isRepeatInfinite = scenario.isRepeatInfinite,
            maxDurationMin = scenario.maxDurationMin,
            isDurationInfinite = scenario.isDurationInfinite,
            randomize = scenario.randomize,
            scenarioMode = scenario.scenarioMode
        )
        mainRepository.addScenario(newScenario)
    }
    /**
     * Delete a click scenario.
     *
     * This will also delete all child entities associated with the scenario.
     *
     * @param scenario the scenario to be deleted.
     */
    fun deleteScenario(scenario: Scenario) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.deleteScenario(scenario)
        }
    }
}