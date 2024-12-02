package com.galixo.autoClicker.feature.config.ui

import android.content.Context
import android.graphics.Point
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.engine.ScenarioEngine
import com.galixo.autoClicker.feature.config.domain.EditionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuViewModel @Inject constructor(
    private val editionRepository: EditionRepository,
    private val scenarioEngine: ScenarioEngine,
) : ViewModel() {

    val canPlayScenario: Flow<Boolean> =
        editionRepository.editedScenario.map { it?.isValid() ?: false }

    val isPlaying: StateFlow<Boolean> = scenarioEngine.isRunning


    val actionsList: StateFlow<List<Action>> =
        editionRepository.editedScenario.map { scenario ->
            scenario?.actions ?: emptyList()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    fun startTemporaryEdition(scenarioId: Scenario) {
        viewModelScope.launch(Dispatchers.IO) {
            editionRepository.startTemporaryEdition(scenarioId)
        }
    }

    fun saveEditions() {
        val editedScenario = editionRepository.editedScenario.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            when (editedScenario.id.databaseId) {
                DATABASE_ID_INSERTION -> editionRepository.mainRepository.addScenario(editedScenario)
                else -> editionRepository.mainRepository.updateScenario(editedScenario)
            }
        }
    }

    fun stopEdition() {
        editionRepository.stopEdition()
    }

    fun toggleScenarioPlay() {
        viewModelScope.launch {
            if (isPlaying.value) scenarioEngine.stopScenario()
            else {
                val currentScenario = editionRepository.editedScenario.value
                if (currentScenario != null) {
                    scenarioEngine.startTemporaryScenario(currentScenario)
                } else {
                    Log.w(TAG, "No scenario to play")
                }
            }
        }
    }

    fun stopScenarioPlay(): Boolean {
        if (!isPlaying.value) return false

        viewModelScope.launch {
            scenarioEngine.stopScenario()
        }
        return true
    }

    fun createNewClick(context: Context, position: Point): Action.Click =
        editionRepository.actionBuilder.createNewClick(context, position)

    fun createNewSwipe(context: Context, from: Point, to: Point): Action.Swipe =
        editionRepository.actionBuilder.createNewSwipe(context, from, to)


    fun addNewAction(action: Action) {
        editionRepository.addNewAction(action = action)
    }


    fun updateAction(action: Action) {
        editionRepository.updateAction(action)
    }


    fun deleteLastAction() {
        val actions = editionRepository.editedScenario.value?.actions ?: return
        if (actions.isNotEmpty()) deleteLastAction(actions.last())
    }

    private fun deleteLastAction(action: Action) {
        editionRepository.deleteAction(action)
        // saveEditions()
    }

}

private const val TAG = "MainMenuModel"