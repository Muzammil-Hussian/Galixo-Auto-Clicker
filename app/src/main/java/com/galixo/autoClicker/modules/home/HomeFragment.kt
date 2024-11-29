package com.galixo.autoClicker.modules.home

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import com.galixo.autoClicker.databinding.FragmentHomeBinding
import com.galixo.autoClicker.databinding.IncludeScenarioTypeViewBinding
import com.galixo.autoClicker.modules.base.fragment.BaseFragment
import com.galixo.autoClicker.modules.guide.HowToUseActivity
import com.galixo.autoClicker.modules.script.presentation.ui.dialog.ScenarioCreationDialog.Companion.TAG
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioCreationViewModel
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioModeSelectionState
import com.galixo.autoClicker.utils.extensions.updateStrokeColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: ScenarioCreationViewModel by viewModels()

    override fun onViewCreated() {
        setupObservers()
        onClickListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scenarioTypeSelectionState.collect(::updateModeSelectionUI)
            }
        }
    }

    private fun updateModeSelectionUI(selectedMode: ScenarioModeSelectionState) {
        binding.apply {
            singleMode.root.updateStrokeColor(selectedMode.selectedItem == ScenarioMode.SINGLE_MODE)
            multiMode.root.updateStrokeColor(selectedMode.selectedItem == ScenarioMode.MULTI_MODE)
        }
    }

    private fun onClickListeners() {
        binding.apply {
            singleMode.initScenarioModeCard(ScenarioMode.SINGLE_MODE)
            multiMode.initScenarioModeCard(ScenarioMode.MULTI_MODE)

            howToUse.setOnClickListener {
                context?.let {
                    startActivity(
                        Intent(
                            it,
                            HowToUseActivity::class.java
                        )
                    )
                }
            }
            startStopActionBarOverlay.setOnClickListener {
                viewModel.createScenario {

                }
            }
        }
    }

    private fun IncludeScenarioTypeViewBinding.initScenarioModeCard(mode: ScenarioMode) {
        Log.i(TAG, "initScenarioModeCard: mode: $mode")
        when (mode) {
            ScenarioMode.SINGLE_MODE -> {
                image.setImageResource(R.drawable.ic_single_mode)
                title.setText(R.string.item_title_single_mode_scenario)
                description.setText(R.string.item_desc_single_mode_scenario)
            }

            ScenarioMode.MULTI_MODE -> {
                image.setImageResource(R.drawable.ic_multi_mode)
                title.setText(R.string.item_title_multi_mode_scenario)
                description.setText(R.string.item_desc_multi_mode_scenario)
            }
        }

        root.setOnClickListener {
            viewModel.setSelectedMode(mode)
        }
    }
}
