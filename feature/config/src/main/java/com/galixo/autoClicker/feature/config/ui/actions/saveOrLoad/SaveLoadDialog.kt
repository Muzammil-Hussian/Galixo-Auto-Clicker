package com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.galixo.autoClicker.core.common.overlays.base.viewModels
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.feature.config.R
import com.galixo.autoClicker.feature.config.databinding.DialogSaveOrLoadBinding
import com.galixo.autoClicker.feature.config.di.ConfigViewModelsEntryPoint
import com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.adapter.ViewPagerAdapter

class SaveLoadDialog : OverlayDialog(R.style.AppTheme) {

    /** View model for the container dialog. */
    private val viewModel: SaveLoadViewModel by viewModels(
        entryPoint = ConfigViewModelsEntryPoint::class.java,
        creator = { saveLoadViewModel() },
    )

    /** ViewBinding containing the views for this dialog. */
    private lateinit var viewBinding: DialogSaveOrLoadBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogSaveOrLoadBinding.inflate(LayoutInflater.from(context)).apply {
            cancel.setOnClickListener {
                back()
            }
            saveOrLoad.setOnClickListener {
                back()
            }
        }
        return viewBinding.root
    }

    private fun setupViewPagerAndTabs() {

        val adapter = ViewPagerAdapter(viewModel, lifecycleScope)
        viewBinding.apply {
            viewPager.adapter = adapter
            with(tabLayout) {
                setupWithViewPager(viewPager)
                getTabAt(0)?.text = "Save"
                getTabAt(1)?.text = "Load"
            }
        }
    }

    override fun onDialogCreated(dialog: AlertDialog) {
        Log.i(TAG, "onDialogCreated: $viewModel")
        setupViewPagerAndTabs()
    }

}

private const val TAG = "SaveLoadDialog"