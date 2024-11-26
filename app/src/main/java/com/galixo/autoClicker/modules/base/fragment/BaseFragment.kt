package com.galixo.autoClicker.modules.base.fragment

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentFragment<T>(bindingFactory)