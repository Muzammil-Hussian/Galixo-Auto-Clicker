package com.galixo.autoClicker.modules.dialog.rename

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RenameScenarioDialogViewModel @Inject constructor(
    private val repository: IMainRepository
) : ViewModel() {

    private val _renameScenario: MutableStateFlow<Scenario?> = MutableStateFlow(null)
    private val renameScenario: Flow<Scenario> = _renameScenario.filterNotNull()

    val name: Flow<String> = renameScenario.map { it.name }.take(1)

    val nameError: Flow<Boolean> = renameScenario.map { it.name.isEmpty() }

    val isValidToRename: Flow<Boolean> = _renameScenario.map {
        println("newName: ${it?.name}")
        it?.name?.isNotEmpty() == true
    }

    fun setRenameScenario(scenario: Scenario) {
        _renameScenario.value = scenario.copy()
    }

    fun getRenameScenario() = _renameScenario.value

    fun setNewName(newName: String) {
        _renameScenario.value = _renameScenario.value?.copy(name = newName)
    }


    fun renameScenario(scenario: Scenario) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateScenario(scenario.copy(name = scenario.name))
    }
}