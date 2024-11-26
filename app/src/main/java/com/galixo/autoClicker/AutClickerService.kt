package com.galixo.autoClicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.galixo.autoClicker.core.common.base.AndroidExecutor
import com.galixo.autoClicker.core.common.base.Dumpable
import com.galixo.autoClicker.core.common.base.requestFilterKeyEvents
import com.galixo.autoClicker.core.common.display.DisplayConfigManager
import com.galixo.autoClicker.core.common.overlays.manager.OverlayManager
import com.galixo.autoClicker.core.scenarios.engine.ScenarioEngine
import com.galixo.autoClicker.localService.ILocalService
import com.galixo.autoClicker.localService.LocalService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.FileDescriptor
import java.io.PrintWriter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * AccessibilityService implementation for the SmartAutoClicker.
 *
 * Started automatically by Android once the user has defined this service has an accessibility service, it provides
 * an API to start and stop the DetectorEngine correctly in order to display the overlay UI and record the screen for
 * clicks detection.
 * This API is offered through the [LocalService] class, which is instantiated in the [LOCAL_SERVICE_INSTANCE] object.
 * This system is used instead of the usual binder interface because an [AccessibilityService] already has its own
 * binder and it can't be changed. To access this local service, use [getLocalService].
 *
 * We need this service to be an accessibility service in order to inject the detected event on the currently
 * displayed activity. This injection is made by the [dispatchGesture] method, which is called everytime an event has
 * been detected.
 */
@AndroidEntryPoint
class AutoClickerService : AccessibilityService(), AndroidExecutor {

    companion object {

        /** The instance of the [ILocalService], providing access for this service to the Activity. */
        private var LOCAL_SERVICE_INSTANCE: ILocalService? = null
            set(value) {
                field = value
                LOCAL_SERVICE_CALLBACK?.invoke(field)
            }

        /** Callback upon the availability of the [LOCAL_SERVICE_INSTANCE]. */
        private var LOCAL_SERVICE_CALLBACK: ((ILocalService?) -> Unit)? = null
            set(value) {
                field = value
                value?.invoke(LOCAL_SERVICE_INSTANCE)
            }

        /**
         * Static method allowing an activity to register a callback in order to monitor the availability of the
         * [ILocalService]. If the service is already available upon registration, the callback will be immediately
         * called.
         *
         * @param stateCallback the object to be notified upon service availability.
         */
        fun getLocalService(stateCallback: ((ILocalService?) -> Unit)?) {
            LOCAL_SERVICE_CALLBACK = stateCallback
        }

        fun isServiceStarted(): Boolean = LOCAL_SERVICE_INSTANCE != null
    }


    private val localService: LocalService?
        get() = LOCAL_SERVICE_INSTANCE as? LocalService



    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var displayConfigManager: DisplayConfigManager

    @Inject
    lateinit var dumbEngine: ScenarioEngine


    private var currentScenarioName: String? = null


    override fun onServiceConnected() {
        super.onServiceConnected()

        LOCAL_SERVICE_INSTANCE = LocalService(
            context = this,
            overlayManager = overlayManager,
            displayConfigManager = displayConfigManager,
            dumbEngine = dumbEngine,
            androidExecutor = this,
            onStart = { name ->
                currentScenarioName = name

                requestFilterKeyEvents(true)
            },
            onStop = {
                currentScenarioName = null
                requestFilterKeyEvents(false)
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            },
            onStateChanged = { isRunning, isMenuHidden -> }
        )
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.i("onUnbind")
        LOCAL_SERVICE_INSTANCE?.stop()
        LOCAL_SERVICE_INSTANCE?.release()
        LOCAL_SERVICE_INSTANCE = null

        return super.onUnbind(intent)
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean =
        localService?.onKeyEvent(event) ?: super.onKeyEvent(event)

    override suspend fun executeGesture(gestureDescription: GestureDescription) {
        suspendCoroutine<Unit?> { continuation ->
            try {
                dispatchGesture(
                    gestureDescription,
                    object : GestureResultCallback() {
                        override fun onCompleted(gestureDescription: GestureDescription?) =
                            continuation.resume(null)

                        override fun onCancelled(gestureDescription: GestureDescription?) {
                            Timber.w("Gesture cancelled: $gestureDescription")
                            continuation.resume(null)
                        }
                    },
                    null,
                )
            } catch (rEx: RuntimeException) {
                Timber.w(
                    rEx,
                    "System is not responsive, the user might be spamming gesture too quickly"
                )
                continuation.resume(null)
            }
        }
    }

    override fun dump(fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        if (writer == null) return

        writer.append("* SmartAutoClickerService:").println()
        writer.append(Dumpable.DUMP_DISPLAY_TAB).append("- isStarted=")
            .append("${(LOCAL_SERVICE_INSTANCE as? LocalService)?.isStarted ?: false}; ")
            .append("scenarioName=")
            .append("$currentScenarioName; ").println()

        displayConfigManager.dump(writer)
        overlayManager.dump(writer)
        dumbEngine.dump(writer)
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
}