package com.galixo.autoClicker.feature.config.ui.actions.savingLoading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ScenarioSavingLoadingViewModel @Inject constructor(
    private val mainRepository: IMainRepository,
    private val editionRepository: EditionRepository
) : ViewModel() {
        /** Only scenario */
        val allScenarios: Flow<List<Scenario>> = mainRepository.scenarios



        fun onDuplicationScenario(scenario: Scenario) = viewModelScope.launch(Dispatchers.IO) {

            val newScenario = Scenario(
                id = Identifier(databaseId = DATABASE_ID_INSERTION, tempId = 0L),
                name = scenario.name,
                actions = scenario.actions.toList(),
                repeatCount = scenario.repeatCount,
                isRepeatInfinite = scenario.isRepeatInfinite,
                maxDurationMin = scenario.maxDurationMin,
                isDurationInfinite = scenario.isDurationInfinite,
                randomize = scenario.randomize,
                scenarioMode = scenario.scenarioMode
            )
            mainRepository.addScenario(newScenario)
        }

        fun addScenario(
            name: String,
            scenarioMode: ScenarioMode,
            onCreationComplete: (Scenario) -> Unit
        ) = viewModelScope.launch(
            Dispatchers.IO
        ) {
            val scenario =
                Scenario(
                    id = Identifier(databaseId = DATABASE_ID_INSERTION, tempId = 0L),
                    name = name,
                    actions = emptyList(),
                    repeatCount = 1,
                    isRepeatInfinite = false,
                    maxDurationMin = 1,
                    isDurationInfinite = true,
                    randomize = false,
                    scenarioMode = scenarioMode
                )

            mainRepository.addScenario(scenario).also {
                // Invoke the callback with the new scenario's database ID
                withContext(Dispatchers.Main) {
                    onCreationComplete.invoke(scenario)
                }
            }
        }

        /**
         * Rename a click scenario.
         *
         * @param item the scenario to be renamed.
         */
        fun renameScenario(item: Scenario, newName: String) {
            viewModelScope.launch(Dispatchers.IO) {
                editionRepository.updateScenario(item.copy(name = newName))
            }
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