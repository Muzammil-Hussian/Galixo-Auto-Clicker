package com.galixo.autoClicker.modules.script.presentation.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.scenarios.domain.model.ScenarioMode
import com.galixo.autoClicker.databinding.DialogScenarioCreationBinding
import com.galixo.autoClicker.databinding.IncludeModeTypeBinding
import com.galixo.autoClicker.modules.script.presentation.ui.listener.Listener
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioCreationViewModel
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioModeItem
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioModeSelectionState
import com.galixo.autoClicker.utils.extensions.updateCardBackgroundColor
import com.galixo.autoClicker.utils.extensions.updateStrokeColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScenarioCreationDialog : DialogFragment() {

    companion object {
        const val TAG = "CreateScenarioDialog"
    }

    private val viewModel by viewModels<ScenarioCreationViewModel>()

    private lateinit var viewBinding: DialogScenarioCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.scenarioTypeSelectionState.collect(::updateTypeSelection) }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogScenarioCreationBinding.inflate(layoutInflater).apply {
            scenarioSingleMode.initScenarioModeCard(ScenarioMode.SINGLE_MODE)
            scenarioMultiMode.initScenarioModeCard(ScenarioMode.MULTI_MODE)
            start.setOnClickListener {
                viewModel.createScenario { (requireActivity() as? Listener)?.startScenario(it) }
                dismiss()
            }
        }

        return createDialog(viewBinding.root)
    }


    private fun createDialog(root: View): BottomSheetDialog =
        BottomSheetDialog(requireContext()).apply {
            setContentView(root)
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    this@ScenarioCreationDialog.dismiss()
                    true
                } else false
            }

            create()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    private fun IncludeModeTypeBinding.initScenarioModeCard(mode: ScenarioMode) {
        when (mode) {
            ScenarioMode.SINGLE_MODE -> {
                title.setText(R.string.item_title_single_mode_scenario)
                description.setText(R.string.item_desc_single_mode_scenario)
                imageScenarioType.setImageResource(R.drawable.ic_single_mode)
            }

            ScenarioMode.MULTI_MODE -> {
                title.setText(R.string.item_title_multi_mode_scenario)
                description.setText(R.string.item_desc_multi_mode_scenario)
                imageScenarioType.setImageResource(R.drawable.ic_multi_mode)
            }
        }

        root.setOnClickListener {
            viewModel.setSelectedMode(mode)
        }

    }

    private fun updateTypeSelection(state: ScenarioModeSelectionState) {
        viewBinding.apply {
            scenarioSingleMode.setState(state.singleModel, state.selectedItem, ScenarioMode.SINGLE_MODE)
            scenarioMultiMode.setState(state.multiMode, state.selectedItem, ScenarioMode.MULTI_MODE)
        }
    }


    private fun IncludeModeTypeBinding.setState(
        item: ScenarioModeItem,
        scenarioMode: ScenarioMode,
        modeType: ScenarioMode
    ) {
        val isSelected = scenarioMode == modeType
        selectedMode.isChecked = isSelected
        title.setText(item.titleRes)
        description.setText(item.descriptionText)
        imageScenarioType.setImageResource(item.iconRes)
        root.apply {
            isClickable = isSelected.not()
            updateStrokeColor(isSelected)
            updateCardBackgroundColor(isSelected)
            root.setOnClickListener { viewModel.setSelectedMode(modeType) }
        }
    }
}