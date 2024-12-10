package com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save

import android.content.Context
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.viewModels
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.galixo.autoClicker.core.common.ui.bindings.fields.setError
import com.galixo.autoClicker.core.common.ui.bindings.fields.setLabel
import com.galixo.autoClicker.core.common.ui.bindings.fields.setOnTextChangedListener
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.FragmentSaveBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import kotlinx.coroutines.launch


class SaveActionContent(appContext: Context) : NavBarDialogContent(appContext) {
    /** View binding for all views in this content. */
    private lateinit var viewBinding: FragmentSaveBinding

    private val dialogViewModel: SaveActionContentViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { saveActionContentViewModel() }
    )

    override fun onCreateView(container: ViewGroup): ViewGroup {
        viewBinding = FragmentSaveBinding.inflate(LayoutInflater.from(context), container, false).apply {
            fieldName.apply {
                setLabel(R.string.input_field_label_scenario_name)
                setOnTextChangedListener { dialogViewModel.setScenarioName(it.toString()) }
                textField.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(context.resources.getInteger(R.integer.name_max_length))
                )
            }
            dialogController.hideSoftInputOnFocusLoss(fieldName.textField)
        }

        return viewBinding.root
    }

    override fun onViewCreated() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { dialogViewModel.scenarioName.collect(viewBinding.fieldName::setText) }
                launch { dialogViewModel.scenarioNameError.collect(viewBinding.fieldName::setError) }
            }
        }
    }

    override fun onDialogButtonClicked(buttonType: DialogNavigationButton) {
        super.onDialogButtonClicked(buttonType)
        Log.i(TAG, "onDialogButtonClicked: $buttonType")
    }
}

private const val TAG = "SaveActionContent"