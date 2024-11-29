package com.galixo.autoClicker.modules.script.presentation.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.galixo.autoClicker.R
import com.galixo.autoClicker.feature.config.databinding.DialogCreatScriptFabBinding

class ScriptCreationFABDialog(
    private val onCreateNew: () -> Unit,
    private val onImportScript: () -> Unit

) : DialogFragment() {

    private var _binding: DialogCreatScriptFabBinding? = null
    private val binding get() = _binding!!

    private var anchorView: View? = null
    private var overlayView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogCreatScriptFabBinding.inflate(layoutInflater).apply {

            createNew.setOnClickListener {
                onCreateNew.invoke()
                dismiss()
            }

            importScript.setOnClickListener {
                onImportScript.invoke()
                dismiss()
            }

            closeDialog.setOnClickListener { dismiss() }
        }

        return Dialog(requireContext()).apply {
            setContentView(binding.root)
            setCanceledOnTouchOutside(false)
            window?.apply {
                setLayout(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setDimAmount(0.0f)
            addOverlayView()
            anchorView?.let { positionDialog(it, this) }
        }
    }

    private fun positionDialog(anchor: View, window: Window) {
        val displayMetrics = resources.displayMetrics
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        val buttonX = location[0]
        val buttonY = location[1]
        val buttonWidth = anchor.width
        val buttonHeight = anchor.height

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Measure dialog dimensions
        binding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val dialogWidth = binding.root.measuredWidth
        val dialogHeight = binding.root.measuredHeight

        val marginRight = resources.getDimensionPixelSize(R.dimen.margin_horizontal_default)
        val padding = resources.getDimensionPixelSize(R.dimen.margin_horizontal_extra_large)

        val xOffset = calculateXOffset(buttonX, buttonWidth, dialogWidth, screenWidth, marginRight)
        val yOffset = calculateYOffset(buttonY, buttonHeight, dialogHeight, screenHeight, padding)

        window.attributes = window.attributes?.apply {
            gravity = Gravity.TOP or Gravity.START
            x = xOffset
            y = yOffset
        }
    }

    private fun calculateXOffset(buttonX: Int, buttonWidth: Int, dialogWidth: Int, screenWidth: Int, marginRight: Int): Int {
        return if ((screenWidth - buttonX - buttonWidth) < (dialogWidth + marginRight)) {
            buttonX + buttonWidth - dialogWidth - marginRight
        } else {
            buttonX
        }
    }

    private fun calculateYOffset(buttonY: Int, buttonHeight: Int, dialogHeight: Int, screenHeight: Int, padding: Int): Int {
        return if (screenHeight - (buttonY + buttonHeight) < dialogHeight) {
            buttonY - dialogHeight - padding
        } else {
            buttonY + buttonHeight + padding
        }
    }

    private fun addOverlayView() {
        if (overlayView != null) return

        overlayView = View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#7A000000"))
            isClickable = true
        }
        (requireActivity().window.decorView as? ViewGroup)?.addView(overlayView)
    }

    private fun removeOverlayView() {
        overlayView?.let {
            (requireActivity().window.decorView as? ViewGroup)?.removeView(it)
            overlayView = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeOverlayView()
        _binding = null
    }

    companion object {
        fun showDialog(fragmentManager: FragmentManager, anchorView: View, onCreateNew: () -> Unit, onImportScript: () -> Unit) {
            ScriptCreationFABDialog(onCreateNew, onImportScript).apply {
                this.anchorView = anchorView
            }.show(fragmentManager, "CreateScriptDialogFragment")
        }
    }
}

