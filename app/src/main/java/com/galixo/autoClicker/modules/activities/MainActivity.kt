package com.galixo.autoClicker.modules.activities

import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.ActivityMainBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.modules.script.presentation.ui.listener.Listener
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioViewModel
import com.galixo.autoClicker.modules.settings.SettingFragment
import com.galixo.autoClicker.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), Listener {

    /** ViewModel providing the click scenarios data to the UI. */
    private val scenarioViewModel: ScenarioViewModel by viewModels()

    /** Scenario clicked by the user. */
    private var requestedItem: Scenario? = null

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    override fun onCreated() {

        setSupportActionBar(binding.toolbar)
        scenarioViewModel.stopScenario()

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_script))
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_script) {
                binding.toolbar.navigationIcon = null
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                binding.toolbar.setNavigationIcon(R.drawable.ic_back_press)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPress()
        }
    }

    private fun onBackPress() {
        val currentDestination = navController.currentDestination
        if (currentDestination?.id == R.id.navigation_setting) {
            navHostFragment.childFragmentManager.fragments.firstOrNull()?.let {
                if (it is SettingFragment) {
                    it.handleBackPress()
                    return
                }
            }
        }

        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp() || super.onSupportNavigateUp()

    override fun startScenario(item: Scenario) {
        requestedItem = item

        scenarioViewModel.startPermissionFlowIfNeeded(
            activity = this@MainActivity, onAllGranted = ::onMandatoryPermissionsGranted
        )
    }

    private fun onMandatoryPermissionsGranted() {
        Log.i(TAG, "onMandatoryPermissionsGranted: ")
        requestedItem?.let { startScenarioResult(it) }
    }


    private fun startScenarioResult(scenario: Scenario) {
        handleScenarioStartResult(
            scenarioViewModel.loadScenario(this@MainActivity, scenario)
        )
    }

    private fun handleScenarioStartResult(result: Boolean) {
        if (result) {
            Log.i(TAG, "All permissions are granted you can do some operations here.")
        } else showToast(R.string.toast_denied_foreground_permission)
    }
}

private const val TAG = "MainActivity"