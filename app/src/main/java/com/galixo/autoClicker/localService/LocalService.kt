package com.galixo.autoClicker.localService

import android.content.Context
import android.view.KeyEvent
import com.galixo.autoClicker.core.common.base.AndroidExecutor
import com.galixo.autoClicker.core.common.display.DisplayConfigManager
import com.galixo.autoClicker.core.common.overlays.manager.OverlayManager
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.engine.ScenarioEngine
import com.galixo.autoClicker.feature.config.ui.MainMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LocalService(
    private val context: Context,
    private val overlayManager: OverlayManager,
    private val displayConfigManager: DisplayConfigManager,
    private val dumbEngine: ScenarioEngine,
    private val androidExecutor: AndroidExecutor,
    private val onStart: (name: String) -> Unit,
    private val onStop: () -> Unit,
    onStateChanged: (isRunning: Boolean, isMenuHidden: Boolean) -> Unit,
) : ILocalService {

    /** Scope for this LocalService. */
    private val serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /** Coroutine job for the delayed start of engine & ui. */
    private var startJob: Job? = null

    /** State of this LocalService. */
    private var state: LocalServiceState =
        LocalServiceState(isStarted = false, isSmartLoaded = false)

    /** True if the overlay is started, false if not. */
    internal val isStarted: Boolean
        get() = state.isStarted


    init {
        dumbEngine.isRunning
            .onEach { dumbIsRunning ->
                onStateChanged(dumbIsRunning, overlayManager.isStackHidden())
            }.launchIn(serviceScope)

        overlayManager.onVisibilityChangedListener = {
            onStateChanged(dumbEngine.isRunning.value, overlayManager.isStackHidden())
        }

    }

    override fun startScenario(scenario: Scenario) {


        if (state.isStarted) return
        state = LocalServiceState(isStarted = true, isSmartLoaded = false)
        onStart(scenario.name)

        displayConfigManager.startMonitoring(context)

        startJob = serviceScope.launch {
            delay(500)

            dumbEngine.init(androidExecutor, scenario)

            overlayManager.navigateTo(
                context = context,
                newOverlay = MainMenu(scenario) { stop() },
            )
        }
    }

    override fun stop() {
        if (!isStarted) return
        state = LocalServiceState(isStarted = false, isSmartLoaded = false)

        serviceScope.launch {
            startJob?.join()
            startJob = null

            dumbEngine.release()
            overlayManager.closeAll(context)
            displayConfigManager.stopMonitoring(context)

            onStop()
        }
    }

    override fun release() {
        serviceScope.cancel()
    }

    fun onKeyEvent(event: KeyEvent?): Boolean {
        event ?: return false
        return overlayManager.propagateKeyEvent(event)
    }
}

private data class LocalServiceState(
    val isStarted: Boolean,
    val isSmartLoaded: Boolean
)