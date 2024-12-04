package com.galixo.autoClicker.feature.config.ui.actions.savingLoading.save

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.feature.config.databinding.FragmentSaveBinding


class SaveActionContent(appContext: Context) : NavBarDialogContent(appContext) {
    /** View binding for all views in this content. */
    private lateinit var viewBinding: FragmentSaveBinding

    override fun onCreateView(container: ViewGroup): ViewGroup {
        viewBinding = FragmentSaveBinding.inflate(LayoutInflater.from(context), container, false).apply {

            fieldName.setText("Script 1")
        }

        return viewBinding.root
    }

    override fun onViewCreated() {
    }

    override fun onDialogButtonClicked(buttonType: DialogNavigationButton) {
        super.onDialogButtonClicked(buttonType)
        Log.i(TAG, "onDialogButtonClicked: $buttonType")
    }
}

private const val TAG = "SaveActionContent"