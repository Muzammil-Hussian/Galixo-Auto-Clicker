package com.galixo.autoClicker.core.common.overlays.dialog.implementation.tab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.lifecycle.Lifecycle
import com.galixo.autoClicker.core.common.overlays.databinding.DialogBaseNavBarBinding
import com.galixo.autoClicker.core.common.overlays.dialog.bottomSheet.OverlayDialogSheet
import com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar.NavBarDialogContent
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout

abstract class TabDialog(@StyleRes theme: Int) : OverlayDialogSheet(theme) {

    /** Map of tab item id to their content view. */
    private val contentMap: MutableMap<Int, NavBarDialogContent> = mutableMapOf()

    private lateinit var baseViewBinding: DialogBaseNavBarBinding
    private lateinit var tabLayout: TabLayout

    abstract fun inflateMenu(tabLayout: TabLayout)

    abstract fun onCreateContent(tabItemId: Int): NavBarDialogContent

    abstract fun onDialogButtonPressed(buttonType: DialogNavigationButton)

    open fun onContentViewChanged(tabItemId: Int) = Unit

    override fun onCreateView(): ViewGroup {
        baseViewBinding = DialogBaseNavBarBinding.inflate(LayoutInflater.from(context)).apply {
            with(actionButtons) {
                actionCancel.setDebouncedOnClickListener { handleButtonClick(DialogNavigationButton.DISMISS) }
                actionDone.setDebouncedOnClickListener { handleButtonClick(DialogNavigationButton.SAVE) }
            }
        }

        tabLayout = baseViewBinding.tabLayout

        // Generic setup of the tabs
        tabLayout.apply {
            inflateMenu(this)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        updateContentView(it.position)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        return baseViewBinding.root
    }

    override fun onDialogCreated(dialog: BottomSheetDialog) {
        updateContentView(
            tabId = tabLayout.selectedTabPosition,
        )

    }

    override fun onStart() {
        super.onStart()
        contentMap[tabLayout.selectedTabPosition]?.resume()
    }

    override fun onStop() {
        super.onStop()
        contentMap[tabLayout.selectedTabPosition]?.pause()
    }

    override fun onDestroy() {
        contentMap.values.forEach { content ->
            content.destroy()
        }
        contentMap.clear()
        super.onDestroy()
    }

    private fun createContentView(tabId: Int): NavBarDialogContent =
        onCreateContent(tabId).apply {
            create(this@TabDialog, baseViewBinding.dialogContent, tabId)
        }

    private fun updateContentView(tabId: Int) {
        contentMap[tabId]?.apply {
            pause()
            stop()
        }

        // Remove all views from the content container to avoid overlap
        baseViewBinding.dialogContent.removeAllViews()

        // Get new content. If it does not exist yet, create it.
        var content = contentMap[tabId]
        if (content == null) {
            content = createContentView(tabId)
            contentMap[tabId] = content
        }

        content.start()
        onContentViewChanged(tabId)


        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) content.resume()
    }

    internal fun debounceInteraction(interaction: () -> Unit) {
        debounceUserInteraction(interaction)
    }

    private fun handleButtonClick(buttonType: DialogNavigationButton) {
        // First notify the contents.
        contentMap.values.forEach { contentInfo ->
            contentInfo.onDialogButtonClicked(buttonType)
        }

        // Then, notify the dialog
        onDialogButtonPressed(buttonType)
    }
}

private const val TAG = "TabDialogLogs"