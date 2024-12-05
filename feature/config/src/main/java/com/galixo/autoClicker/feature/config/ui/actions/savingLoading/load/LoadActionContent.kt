package com.galixo.autoClicker.feature.config.ui.actions.savingLoading.load

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.base.extensions.beGoneIf
import com.galixo.autoClicker.core.common.base.extensions.beVisibleIf
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.databinding.FragmentLoadBinding
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.ScenarioSavingLoadingViewModel
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.adapter.LoadListAdapter
import com.galixo.autoClicker.feature.config.ui.actions.savingLoading.callback.ScriptItemCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoadActionContent(appContext: Context, private val viewModel: ScenarioSavingLoadingViewModel) : NavBarDialogContent(appContext) {

    /** View binding for all views in this content. */
    private lateinit var viewBinding: FragmentLoadBinding

    private val adapter by lazy {
        LoadListAdapter(object : ScriptItemCallback {
            override fun onItemMenuClick(menuItem: MenuItem, item: Scenario) {}

            override fun onStartScenario(item: Scenario) {}
        })
    }

    override fun onCreateView(container: ViewGroup): ViewGroup {
        viewBinding = FragmentLoadBinding.inflate(LayoutInflater.from(context), container, false).apply {

            lifecycleScope.launch {
                viewModel.allScenarios.collectLatest {


                }
            }
        }
        return viewBinding.root
    }

    override fun onViewCreated() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.allScenarios.collect(::updateScenariosList) }
            }
        }
    }

    private fun updateScenariosList(scenariosList: List<Scenario>) {
        viewBinding.apply {
            list.adapter = adapter
            Log.i(TAG, "onCreateView: ${scenariosList.size}")
            layoutEmpty.beGoneIf(scenariosList.isNotEmpty())
            list.beVisibleIf(scenariosList.isNotEmpty())
            adapter.submitList(scenariosList)
        }
    }


    override fun onDialogButtonClicked(buttonType: DialogNavigationButton) {
        super.onDialogButtonClicked(buttonType)
        Log.i(TAG, "onDialogButtonClicked: $buttonType")
    }
}

private const val TAG = "LoadActionContent"