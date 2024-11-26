package com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewpager.widget.PagerAdapter
import com.galixo.autoClicker.core.common.ui.bindings.fields.setText
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.databinding.FragmentLoadBinding
import com.galixo.autoClicker.feature.config.databinding.FragmentSaveBinding
import com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.SaveLoadViewModel
import com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.callback.ScriptItemCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ViewPagerAdapter(private val viewModel: SaveLoadViewModel, private val lifecycleScope: LifecycleCoroutineScope) : PagerAdapter() {

    override fun getCount(): Int = 2

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = when (position) {
            0 -> createSaveFragmentView(container)
            1 -> createLoadFragmentView(container)
            else -> throw IllegalStateException("Unexpected position $position")
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    private fun createSaveFragmentView(container: ViewGroup): View {

        val view = FragmentSaveBinding.inflate(LayoutInflater.from(container.context), container, false).apply {
            fieldName.setText("MyScript")
        }

        return view.root
    }


    private fun createLoadFragmentView(container: ViewGroup): View {
        val view = FragmentLoadBinding.inflate(LayoutInflater.from(container.context), container, false).apply {

            lifecycleScope.launch {
                viewModel.allScenarios.collectLatest {

                    val adapter = LoadListAdapter(object : ScriptItemCallback {
                        override fun onItemMenuClick(menuItem: MenuItem, item: Scenario) {

                        }

                        override fun onStartScenario(item: Scenario) {

                        }
                    })

                    list.adapter = adapter
                    Log.i(TAG, "createLoadFragmentView: $it")

                    if (it.isEmpty()) {
                        list.visibility = View.GONE
                        layoutEmpty.visibility = View.VISIBLE
                    } else {
                        list.visibility = View.VISIBLE
                        layoutEmpty.visibility = View.GONE
                    }
                    adapter.submitList(it)
                }
            }

        }

        return view.root
    }

}

private const val TAG = "ViewPagerAdapter"