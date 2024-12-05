package com.galixo.autoClicker.feature.config.domain

import android.util.Log
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditionRepository @Inject constructor(
    val mainRepository: IMainRepository,
) {

    private val _editedScenario: MutableStateFlow<Scenario?> = MutableStateFlow(null)
    val editedScenario: StateFlow<Scenario?> = _editedScenario

    val actionBuilder: EditedActionsBuilder = EditedActionsBuilder()


    /** Start editing a temporary scenario. */
    fun startTemporaryEdition(scenario: Scenario): Boolean {
        _editedScenario.value = scenario
        actionBuilder.startEdition(scenario.id)
        return true
    }

    /** Save editions changes in the database. */
    suspend fun saveEditions() {
        val scenarioToSave = _editedScenario.value ?: return

        when (scenarioToSave.id.databaseId) {
            DATABASE_ID_INSERTION -> mainRepository.addScenario(scenarioToSave)
            else -> mainRepository.updateScenario(scenarioToSave)
        }

        stopEdition()
    }

    fun stopEdition() {
        Log.d(TAG, "Stop editions")

        _editedScenario.value = null
        actionBuilder.clearState()
    }

    fun updateScenario(scenario: Scenario) {
        Log.d(TAG, "Updating scenario with $scenario")
        _editedScenario.value = scenario
    }

    /**
     * always add new action at lastIndex+1
     */
    fun addNewAction(action: Action) {
        val editedScenario = _editedScenario.value ?: return
        Log.d(TAG, "Add action to edited scenario $action")
        val previousActions = editedScenario.actions
        Log.i(TAG, "addNewAction: PreviousActions: ${previousActions.size}")
        _editedScenario.value = editedScenario.copy(
            actions = previousActions.toMutableList().apply {
                Log.i(TAG, "addNewAction: priority: ${previousActions.lastIndex + 1}")
                add(action.copyWithNewPriority(previousActions.lastIndex + 1))
            }
        )
        Log.i(TAG, "addNewAction: newActions: ${editedScenario.actions.size}")

    }

    fun updateAction(action: Action) {
        val editedScenario = _editedScenario.value ?: return
        val actionIndex = editedScenario.actions.indexOfFirst { it.id == action.id }
        if (actionIndex == -1) {
            Log.w(TAG, "Can't update action, it is not in the edited scenario.")
            return
        }

        _editedScenario.value = editedScenario.copy(
            actions = editedScenario.actions.toMutableList().apply {
                set(actionIndex, action)
            }
        )
    }

    fun deleteAction(action: Action) {
        val editedScenario = _editedScenario.value ?: return
        val deleteIndex = editedScenario.actions.indexOfFirst { it.id == action.id }

        Log.d(TAG, "Delete action from edited scenario $action at $deleteIndex")
        _editedScenario.value = editedScenario.copy(
            actions = editedScenario.actions.toMutableList().apply {
                removeAt(deleteIndex)

                if (deleteIndex > lastIndex) return@apply
                updatePriorities(deleteIndex..lastIndex)
            }
        )
    }

    fun updateActions(actions: List<Action>) {
        val editedScenario = _editedScenario.value ?: return

        Log.d(TAG, "Updating action list with $actions")
        _editedScenario.value = editedScenario.copy(
            actions = actions.toMutableList().apply { updatePriorities() }
        )
    }

    private fun Action.copyWithNewPriority(priority: Int): Action =
        when (this) {
            is Action.Click -> copy(priority = priority)
            is Action.Swipe -> copy(priority = priority)
        }

    private fun MutableList<Action>.updatePriorities(range: IntRange = indices) {
        for (index in range) {
            Log.d(TAG, "Updating priority to $index for action ${get(index)}")
            set(index, get(index).copyWithNewPriority(index))
        }
    }
}

/** Tag for logs */
private const val TAG = "EditionRepository"