package com.galixo.autoClicker.modules.settings.dialog.repeatMode

import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.modules.settings.dialog.repeatMode.enum.RepeatMode
import com.galixo.autoClicker.modules.settings.dialog.repeatMode.model.RepeatModeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RepeatModeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(RepeatModeState())
    val state: StateFlow<RepeatModeState> = _state

    fun updateRepeatMode(mode: RepeatMode) = _state.update { it.copy(selectedMode = mode) }

    fun updateTimeDuration(hours: Int, minutes: Int, seconds: Int) = _state.update { it.copy(hours = hours, minutes = minutes, seconds = seconds) }

    fun updateRepeats(repeats: Int) = _state.update { it.copy(repeats = repeats) }

}


