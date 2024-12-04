package com.galixo.autoClicker.modules.script.presentation.viewModel

import android.content.Context
import android.graphics.Point
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.common.base.identifier.DATABASE_ID_INSERTION
import com.galixo.autoClicker.core.common.base.identifier.Identifier
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import com.galixo.autoClicker.feature.config.domain.getDefaultClickDurationMs
import com.galixo.autoClicker.feature.config.domain.getDefaultClickName
import com.galixo.autoClicker.feature.config.domain.getDefaultClickRepeatCount
import com.galixo.autoClicker.feature.config.domain.getDefaultClickRepeatDelay
import com.galixo.autoClicker.feature.config.domain.getDefaultRandomize
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ScenarioCreationViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {


    private val _selectedType: MutableStateFlow<ScenarioMode> =
        MutableStateFlow(ScenarioMode.MULTI_MODE)

    val scenarioTypeSelectionState: Flow<ScenarioModeSelectionState> =
        _selectedType.map { selectedType ->
            ScenarioModeSelectionState(
                singleModel = ScenarioModeItem.SingleModel,
                multiMode = ScenarioModeItem.MultiMode,
                selectedItem = selectedType,
            )
        }

    fun setSelectedMode(scenarioMode: ScenarioMode) {
        _selectedType.value = scenarioMode
    }

    fun createScenario(callback: (Scenario) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val scenarioMode = when (_selectedType.value) {
                ScenarioMode.SINGLE_MODE -> ScenarioMode.SINGLE_MODE
                ScenarioMode.MULTI_MODE -> ScenarioMode.MULTI_MODE
            }

            createScenario(scenarioMode).also {
                withContext(Dispatchers.Main) { callback.invoke(it) }
            }
        }
    }

    private fun createScenario(scenarioMode: ScenarioMode): Scenario {
        val id = Identifier(databaseId = DATABASE_ID_INSERTION, tempId = 0L)

        val scenario = Scenario(
            id = id,
            name = context.getString(R.string.default_scenario_name),
            actions = if (scenarioMode == ScenarioMode.SINGLE_MODE) {
                listOf(
                    Action.Click(
                        id = id,
                        scenarioId = id,
                        name = context.getDefaultClickName(),
                        position = Point(300, 500),
                        pressDurationMs = context.getDefaultClickDurationMs(),
                        repeatCount = context.getDefaultClickRepeatCount(),
                        isRepeatInfinite = false,
                        repeatDelayMs = context.getDefaultClickRepeatDelay(),
                    ),
                )
            } else emptyList(),
            repeatCount = 1,
            isRepeatInfinite = true,
            maxDurationMin = 1,
            isDurationInfinite = true,
            randomize = context.getDefaultRandomize(),
            scenarioMode = scenarioMode
        )
        return scenario
    }
}

data class ScenarioModeSelectionState(
    val singleModel: ScenarioModeItem.SingleModel,
    val multiMode: ScenarioModeItem.MultiMode,
    val selectedItem: ScenarioMode
)

sealed class ScenarioModeItem(val titleRes: Int, val iconRes: Int, val descriptionText: Int) {

    data object SingleModel : ScenarioModeItem(
        titleRes = R.string.item_title_single_mode_scenario,
        iconRes = R.drawable.ic_single_mode,
        descriptionText = R.string.item_desc_single_mode_scenario,
    )

    data object MultiMode : ScenarioModeItem(
        titleRes = R.string.item_title_multi_mode_scenario,
        iconRes = R.drawable.ic_multi_mode,
        descriptionText = R.string.item_desc_multi_mode_scenario
    )
}
