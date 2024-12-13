package com.galixo.autoClicker.modules.base.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.galixo.autoClicker.core.common.theme.viewModel.ThemeUiState
import com.galixo.autoClicker.core.common.theme.viewModel.ThemeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "BaseActivity"

abstract class BaseActivity<T : ViewBinding>(private val bindingFactory: (LayoutInflater) -> T) : AppCompatActivity() {

    protected val binding by lazy { bindingFactory(layoutInflater) }
    private val themeViewModel: ThemeViewModel by viewModels()
    protected var includeTopPadding = false
    protected var includeBottomPadding = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe the theme and apply it on startup
        lifecycleScope.launch {
            themeViewModel.themeUiState.collectLatest { themeUiState ->
                if (themeUiState is ThemeUiState.Success) {
                    themeViewModel.applyTheme(themeUiState.theme)
                }
            }
        }

        setContentView(binding.root)
        onCreated()
    }

    /**
     * @param type
     *     0: Show SystemBars
     *     1: Hide StatusBars
     *     2: Hide NavigationBars
     *     3: Hide SystemBars
     */

    protected open fun hideStatusBar(type: Int) {
        Log.d(TAG, "hideStatusBar: Showing/Hiding: Type: $type")
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = when (type) {
                0 -> WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                else -> WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            when (type) {
                0 -> show(WindowInsetsCompat.Type.systemBars())
                1 -> hide(WindowInsetsCompat.Type.systemBars())
                2 -> hide(WindowInsetsCompat.Type.statusBars())
                3 -> hide(WindowInsetsCompat.Type.navigationBars())
                else -> hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    abstract fun onCreated()
}