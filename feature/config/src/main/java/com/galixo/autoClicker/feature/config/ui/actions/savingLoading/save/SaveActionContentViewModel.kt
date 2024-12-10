package com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save

import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SaveActionContentViewModel @Inject constructor(private val editionRepository: EditionRepository) : ViewModel() {

    private val userModifications: StateFlow<Scenario?> = editionRepository.editedScenario

    /** The event name value currently edited by the user. */
    val scenarioName: Flow<String> = userModifications
        .filterNotNull()
        .map { it.name }
        .take(1)

    /** Tells if the scenario name is valid or not. */
    val scenarioNameError: Flow<Boolean> = userModifications
        .map { it?.name?.isEmpty() == true }

    fun setScenarioName(newName: String) {
        userModifications.value?.copy(name = newName)?.let {
            editionRepository.updateScenario(it)
        }
    }

}