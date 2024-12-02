package com.galixo.autoClicker.core.common.overlays.dialog.implementation.navBar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.lifecycle.Lifecycle
import com.galixo.autoClicker.core.common.overlays.databinding.DialogBaseNavBarBinding
import com.galixo.autoClicker.core.common.overlays.dialog.OverlayDialog
import com.galixo.autoClicker.core.common.ui.bindings.dialogs.DialogNavigationButton
import com.google.android.material.navigation.NavigationBarView

abstract class NavBarDialog(@StyleRes theme: Int) : OverlayDialog(theme) {

    /** Map of navigation bar item id to their content view.*/
    private val contentMap: MutableMap<Int, NavBarDialogContent> = mutableMapOf()

    private lateinit var baseViewBinding: DialogBaseNavBarBinding

    abstract fun inflateMenu(navBarView: NavigationBarView)

    abstract fun onCreateContent(navItemId: Int): NavBarDialogContent

    abstract fun onDialogButtonPressed(buttonType: DialogNavigationButton)

    open fun onContentViewChanged(navItemId: Int) = Unit

    override fun onCreateView(): ViewGroup {
        baseViewBinding = DialogBaseNavBarBinding.inflate(LayoutInflater.from(context)).apply {

            buttonDismiss.setDebouncedOnClickListener { handleButtonClick(DialogNavigationButton.DISMISS) }
            buttonSave.setDebouncedOnClickListener { handleButtonClick(DialogNavigationButton.SAVE) }

            navBarView.apply {
                inflateMenu(this)
                setOnItemSelectedListener { item ->
                    updateContentView(item.itemId)
                    true
                }
            }
        }

        return baseViewBinding.root
    }

    @CallSuper
    override fun onDialogCreated(dialog: androidx.appcompat.app.AlertDialog) {
        updateContentView(
            itemId = baseViewBinding.navBarView.selectedItemId,
            forceUpdate = true,
        )
    }

    override fun onStart() {
        super.onStart()
        contentMap[baseViewBinding.navBarView.selectedItemId]?.resume()
    }

    override fun onStop() {
        super.onStop()
        contentMap[baseViewBinding.navBarView.selectedItemId]?.pause()
    }

    override fun onDestroy() {
        contentMap.values.forEach { content ->
            content.destroy()
        }
        contentMap.clear()
        super.onDestroy()
    }


    private fun createContentView(itemId: Int): NavBarDialogContent =
        onCreateContent(itemId).apply {
            create(this@NavBarDialog, baseViewBinding.dialogContent, itemId)
        }

    private fun updateContentView(itemId: Int, forceUpdate: Boolean = false) {
        if (!forceUpdate && baseViewBinding.navBarView.selectedItemId == itemId) return

        // Get the current content and stop it, if any.
        contentMap[baseViewBinding.navBarView.selectedItemId]?.apply {
            pause()
            stop()
        }

        // Get new content. If it does not exist yet, create it.
        var content = contentMap[itemId]
        if (content == null) {
            content = createContentView(itemId)
            contentMap[itemId] = content
        }

        content.start()
        onContentViewChanged(itemId)

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

