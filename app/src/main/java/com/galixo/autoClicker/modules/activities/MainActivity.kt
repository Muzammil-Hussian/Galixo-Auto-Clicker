package com.galixo.autoClicker.modules.activities

import android.content.Intent
import android.view.Menu
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.ActivityMainBinding
import com.galixo.autoClicker.modules.base.activity.BaseActivity
import com.galixo.autoClicker.modules.script.presentation.ui.listener.Listener
import com.galixo.autoClicker.modules.script.presentation.viewModel.ScenarioViewModel
import com.galixo.autoClicker.modules.troubleShoot.ui.TroubleShootingActivity
import com.galixo.autoClicker.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), Listener {

    /** ViewModel providing the click scenarios data to the UI. */
    private val scenarioViewModel: ScenarioViewModel by viewModels()

    /** Scenario clicked by the user. */
    private var requestedItem: Scenario? = null

    override fun onCreated() {
        setupNavigation()
        scenarioViewModel.stopScenario()
        binding.toolbar.apply {
            setOnMenuItemClickListener { menuItem ->
                Timber.i("menu item clicked: $menuItem")
                when (menuItem.itemId) {
                    R.id.action_troubleshooting -> {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                TroubleShootingActivity::class.java
                            )
                        )
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_script,
                R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }


    override fun startScenario(item: Scenario) {
        requestedItem = item

        scenarioViewModel.startPermissionFlowIfNeeded(
            activity = this@MainActivity,
            onAllGranted = ::onMandatoryPermissionsGranted
        )
    }

    private fun onMandatoryPermissionsGranted() {
        Timber.i("onMandatoryPermissionsGranted: ")
        requestedItem?.let { startScenarioResult(it) }
    }


    private fun startScenarioResult(scenario: Scenario) {
        handleScenarioStartResult(
            scenarioViewModel.loadScenario(this@MainActivity, scenario)
        )
    }

    private fun handleScenarioStartResult(result: Boolean) {
        if (result) {
            Timber.i("All permissions are granted you can do some operations here.")
        } else showToast(R.string.toast_denied_foreground_permission)
    }
}