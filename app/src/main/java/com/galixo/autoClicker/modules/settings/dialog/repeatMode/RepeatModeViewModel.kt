package com.galixo.autoClicker.modules.settings.dialog.repeatMode

import androidx.lifecycle.ViewModel
import com.galixo.autoClicker.core.common.ui.enum.RepeatMode
import com.galixo.autoClicker.core.common.ui.model.RepeatModeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RepeatModeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(RepeatModeState(RepeatMode.NEVER_STOP))
    val state: StateFlow<RepeatModeState> = _state

    fun updateRepeatMode(mode: RepeatMode) {
        _state.value = _state.value.copy(selectedMode = mode)
    }

    fun updateTimeDuration(hours: Int, minutes: Int, seconds: Int) = _state.value.copy(hours = hours, minutes = minutes, seconds = seconds)

    fun updateRepeats(repeats: Int) = _state.value.copy(repeatCount = repeats)
}


