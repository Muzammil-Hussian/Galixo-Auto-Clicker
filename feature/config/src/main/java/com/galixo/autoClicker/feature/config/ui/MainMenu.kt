package com.galixo.autoClicker.feature.config.ui

import android.annotation.SuppressLint
import android.graphics.Point
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.isStopScenarioKey
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.menu.OverlayMenu
import com.galixo.autoClicker.core.scenarios.domain.model.Action
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.OverlayMainMenuBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.click.ClickPointDialog
import com.galixo.autoClicker.feature.config.ui.actions.save.SaveDialog
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.ScenarioSaveLoadDialog
import com.galixo.autoClicker.feature.config.ui.actions.scenarioConfig.ScenarioConfigDialog
import com.galixo.autoClicker.feature.config.ui.actions.swipe.SwipePointDialog
import com.galixo.autoClicker.feature.config.ui.view.ClickPointView
import com.galixo.autoClicker.feature.config.ui.view.CreateSwipeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MainMenu(
    private val scenario: Scenario,
    private val onStopClicked: () -> Unit,
) : OverlayMenu(theme = R.style.AppTheme) {

    private val clickViews by lazy { mutableListOf<ClickPointView>() }
    private val swipeViews by lazy { mutableListOf<CreateSwipeView>() }

    private var previousActions: List<Action> = emptyList()

    private val viewModel: MainMenuViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { mainMenuViewModel() },
    )

    private lateinit var viewBinding: OverlayMainMenuBinding

    private var keyDownHandled: Boolean = false

    override fun onCreate() {
        super.onCreate()
        viewModel.startTemporaryEdition(scenario)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.isPlaying.collect(::updateMenuPlayingState) }
                launch { viewModel.actionsList.collect(::updateActionsOnScreen) }
                launch { viewModel.canPlayScenario.collect(::updatePlayPauseButtonEnabledState) }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateMenu(layoutInflater: LayoutInflater): ViewGroup {
        viewBinding = OverlayMainMenuBinding.inflate(layoutInflater).apply {
            root.setOnTouchListener { _: View, event: MotionEvent -> onMoveTouched(event) }
        }
        addPointIfSingleModeCreated()
        return viewBinding.root
    }

    private fun addPointIfSingleModeCreated() {
        val isMultiMode = scenario.scenarioMode == ScenarioMode.MULTI_MODE
        setMenuItemVisibility(viewBinding.btnAddClickAction, isMultiMode)
        setMenuItemVisibility(viewBinding.btnAddSwipeAction, isMultiMode)
        setMenuItemVisibility(viewBinding.btnRemoveLastAddedAction, isMultiMode)
        setMenuItemVisibility(viewBinding.lineBottom, isMultiMode)
    }

    override fun onDestroy() {
        removeViews()
        viewModel.stopEdition()
        super.onDestroy()
    }

    private fun removeViews() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                clickViews.forEach { it.remove() }
                swipeViews.forEach { it.remove() }

                withContext(Dispatchers.Main) {
                    clickViews.clear()
                    swipeViews.clear()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "removeViews: exception: ${e.printStackTrace()}", e.cause)
        }
    }

    override fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (!keyEvent.isStopScenarioKey()) return false

        when (keyEvent.action) {
            KeyEvent.ACTION_DOWN -> {
                if (viewModel.stopScenarioPlay()) {
                    keyDownHandled = true
                    return true
                }
            }

            KeyEvent.ACTION_UP -> {
                if (keyDownHandled) {
                    keyDownHandled = false
                    return true
                }
            }
        }

        return false
    }

    private fun updateActionsOnScreen(currentActions: List<Action>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val addedActions = currentActions.filter { it !in previousActions }
            val removedActions = previousActions.filter { it !in currentActions }

            removedActions.forEach { action ->

                when (action) {
                    is Action.Click -> {
                        val viewToRemove = clickViews.find { it.clickDescription.id == action.id }
                        viewToRemove?.remove()
                        clickViews.remove(viewToRemove)

                    }

                    is Action.Swipe -> {
                        val viewToRemove = swipeViews.find { it.swipeDescription.id == action.id }
                        viewToRemove?.remove()
                        swipeViews.remove(viewToRemove)

                    }
                }
            }

            addedActions.forEach { action ->
                withContext(Dispatchers.Main) {
                    when (action) {
                        is Action.Click -> {
                            val clickView = ClickPointView(
                                context,
                                windowManager,
                                displayConfigManager,
                                action,
                                onViewMoved = { updatedPosition ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val updatedAction = action.copy(position = updatedPosition)
                                        viewModel.updateAction(updatedAction)
                                    }
                                },
                                onClickView = {
                                    showClickDialog(action)
                                }
                            )
                            clickViews.add(clickView)
                        }

                        is Action.Swipe -> {
                            val swipeView = CreateSwipeView(
                                context,
                                windowManager,
                                action,
                                onPositionChanged = {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        viewModel.updateAction(it)
                                    }
                                },
                                onPointClick = { _, _ ->
                                    if (viewModel.isPlaying.value.not()) showSwipeDialog(action)
                                }
                            )
                            swipeViews.add(swipeView)
                        }
                    }
                }
            }

            previousActions = currentActions
        }
    }

    private fun showClickDialog(action: Action.Click) {
        lifecycleScope.launch {
            overlayManager.navigateTo(context, ClickPointDialog(action,
                onConfirmClicked = { viewModel.updateAction(it) }, onDismissClicked = {})
            )
        }
    }

    private fun showSwipeDialog(action: Action.Swipe) {
        lifecycleScope.launch {
            overlayManager.navigateTo(
                context, SwipePointDialog(action,
                    onConfirmClicked = {
                        viewModel.updateAction(it)
                    },
                    onDismissClicked = {}
                )
            )
        }
    }

    private fun updatePlayPauseButtonEnabledState(canStartDetection: Boolean) {
        setMenuItemViewEnabled(view = viewBinding.btnPlay, enabled = canStartDetection)
    }

    private fun updateMenuPlayingState(isPlaying: Boolean) {
        clickViews.forEach { it.toggleTouch(!isPlaying) }
        swipeViews.forEach { it.toggleTouch(!isPlaying) }

        viewBinding.btnPlay.setImageResource(
            if (isPlaying) R.drawable.ic_pause
            else R.drawable.ic_play_arrow
        )
    }

    override fun onMenuItemClicked(viewId: Int) {
        when (viewId) {
            R.id.btn_play -> onPlayPauseClicked()
            R.id.btn_add_click_action -> onCreateClickAction()
            R.id.btn_add_swipe_action -> onCreateSwipeAction()
            R.id.btn_remove_last_added_action -> onRemoveLastAction()
            R.id.btn_save_actions -> onSaveActions()
            R.id.btn_action_list -> onScenarioConfigClicked()
            R.id.btn_close -> onCloseClicked()
        }
    }

    private fun onPlayPauseClicked() = lifecycleScope.launch {
        clickViews.forEach { it.toggleTouch(false) }
        swipeViews.forEach { it.toggleTouch(false) }
        delay(100)
        viewModel.toggleScenarioPlay()
    }

    private fun onCreateClickAction() {
        lifecycleScope.launch(Dispatchers.IO) {
            val click = viewModel.createNewClick(
                context, Point(
                    displayConfigManager.displayConfig.sizePx.x / 2,
                    displayConfigManager.displayConfig.sizePx.y / 2
                )
            )
            viewModel.addNewAction(click)
        }
    }

    private fun onCreateSwipeAction() {
        lifecycleScope.launch(Dispatchers.IO) {
            val centerPoint = Point(
                displayConfigManager.displayConfig.sizePx.x / 2,
                displayConfigManager.displayConfig.sizePx.y / 2
            )
            val fromPoint = Point(centerPoint.x - 250, centerPoint.y - 200)
            val toPoint = Point(centerPoint.x + 250, centerPoint.y - 200)

            val swipe = viewModel.createNewSwipe(context = context, from = fromPoint, to = toPoint)

            viewModel.addNewAction(swipe)
        }
    }

    private fun onRemoveLastAction() {
        viewModel.deleteLastAction()
    }

    private fun onSaveActions() {
        overlayManager.navigateTo(
            context = context,
            newOverlay = ScenarioSaveLoadDialog(
                onConfigSaved = {
                    viewModel.saveEditions()
                    onStopClicked.invoke()
                },
                onConfigDiscarded = {}
            ),
            hideCurrent = false
        )
    }

    private fun onCloseClicked() {
        overlayManager.navigateTo(
            context = context,
            newOverlay = SaveDialog(
                onDiscard = {
                    viewModel.stopEdition()
                    onStopClicked.invoke()
                }, onSave = {
                    viewModel.saveEditions()
                    onStopClicked.invoke()
                }), hideCurrent = false
        )
    }

    private fun onScenarioConfigClicked() {
        overlayManager.navigateTo(
            context = context,
            newOverlay = ScenarioConfigDialog(
                onConfigSaved = {},
                onConfigDiscarded = {},
            ),
            hideCurrent = false,
        )
    }
}

private const val TAG = "OverlayMainMenuLogs"