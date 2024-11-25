package com.galixo.autoClicker.core.scenarios.engine

import android.util.Log
import com.galixo.autoClicker.core.common.base.AndroidExecutor
import com.galixo.autoClicker.core.common.base.Dumpable
import com.galixo.autoClicker.core.common.base.addDumpTabulationLvl
import com.galixo.autoClicker.core.scenarios.domain.IMainRepository
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.PrintWriter
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ScenarioEngine @Inject constructor(
    private val mainRepository: IMainRepository,
) : Dumpable {

    /** Execute the actions. */
    private var actionExecutor: ActionExecutor? = null

    /** Coroutine scope for the scenario processing. */
    private var processingScope: CoroutineScope? = null

    /** Job for the scenario auto stop. */
    private var timeoutJob: Job? = null

    /** Job for the scenario execution. */
    private var executionJob: Job? = null

    /** Completion listener on actions tries.*/
    private var onTryCompletedListener: (() -> Unit)? = null

    private val scenarioDbId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val scenario: Flow<Scenario?> =
        scenarioDbId.flatMapLatest { dbId ->
            if (dbId == null) return@flatMapLatest flowOf(null)
            mainRepository.getScenarioFlow(dbId)
        }

    private val _isRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun init(androidExecutor: AndroidExecutor, scenario: Scenario) {
        actionExecutor = ActionExecutor(androidExecutor)
        scenarioDbId.value = scenario.id.databaseId

        processingScope = CoroutineScope(Dispatchers.IO)
    }

    fun startScenario() {
        if (_isRunning.value) return

        processingScope?.launch {
            scenarioDbId.value?.let { dbId ->
                mainRepository.getScenario(dbId)?.let { scenario ->
                    startEngine(scenario)
                }
            }
        }
    }

    fun startTemporaryScenario(scenario: Scenario) {
        processingScope?.launch { startEngine(scenario) }
    }

    fun stopScenario() {
        if (!isRunning.value) return
        _isRunning.value = false

        Log.d(TAG, "stopScenario")

        timeoutJob?.cancel()
        timeoutJob = null
        executionJob?.cancel()
        executionJob = null

        onTryCompletedListener?.invoke()
        onTryCompletedListener = null
    }

    fun release() {
        if (isRunning.value) stopScenario()

        scenarioDbId.value = null
        processingScope?.cancel()
        processingScope = null

        actionExecutor = null
    }

    private fun startEngine(scenario: Scenario) {
        if (_isRunning.value || scenario.actions.isEmpty()) return
        _isRunning.value = true

        Log.d(TAG, "startScenario ${scenario.id} with ${scenario.actions.size} actions")

        if (!scenario.isDurationInfinite) timeoutJob = startTimeoutJob(scenario.maxDurationMin)
        executionJob = startScenarioExecutionJob(scenario)
    }

    private fun startTimeoutJob(timeoutDurationMinutes: Int): Job? =
        processingScope?.launch {
            Log.d(TAG, "startTimeoutJob: timeoutDurationMinutes=$timeoutDurationMinutes")
            delay(timeoutDurationMinutes.minutes.inWholeMilliseconds)

            processingScope?.launch { stopScenario() }
        }

    private fun startScenarioExecutionJob(scenario: Scenario): Job? =
        processingScope?.launch {
            scenario.repeat {
                scenario.actions.forEach { action ->
                    actionExecutor?.executeAction(action, scenario.randomize)
                }
            }

            processingScope?.launch { stopScenario() }
        }

    override fun dump(writer: PrintWriter, prefix: CharSequence) {
        val contentPrefix = prefix.addDumpTabulationLvl()

        writer.apply {
            append(prefix).println("* Engine:")

            append(contentPrefix)
                .append("- scenarioId=${scenarioDbId.value}; ")
                .append("isRunning=${isRunning.value}; ")
                .println()
        }
    }
}

private const val TAG = "Engine"