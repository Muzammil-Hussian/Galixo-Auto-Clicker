package com.galixo.autoClicker.modules.dialog.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.galixo.autoClicker.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class RoundedBottomSheetDialogFragment<B : ViewBinding> :
    BottomSheetDialogFragment() {

    private var _binding: B? = null
    val binding: B get() = _binding!!
    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> B

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (_binding != null) {
            return binding.root
        }

        _binding = bindingInflater.invoke(inflater, container, false)

        initViews()
        setClickListeners()

        return binding.root
    }

    open fun initViews() {}

    open fun setClickListeners() {}
}