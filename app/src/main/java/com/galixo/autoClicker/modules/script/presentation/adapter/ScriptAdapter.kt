package com.galixo.autoClicker.modules.script.presentation.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.galixo.autoClicker.R
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.databinding.ItemScriptBinding


class ScriptAdapter(private val itemListener: ScriptItemCallback) :
    ListAdapter<Scenario, RecyclerView.ViewHolder>(ScriptDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemScriptBinding.inflate(inflater, parent, false)
        return ScriptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ScriptViewHolder).bind(getItem(position))


    inner class ScriptViewHolder(private val binding: ItemScriptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Scenario) {
            binding.apply {
                scriptName.text = item.name
                scriptMode.text = item.scenarioMode.name
                (layoutPosition + 1).apply { itemIndex.text = this.toString() }
                btnPlay.setOnClickListener { itemListener.onStartScenario(item) }

                moreMenu.setOnClickListener {
                    PopupMenu(root.context, this.moreMenu, Gravity.END, 0, R.style.popupMenuStyle).apply {
                        inflate(R.menu.menu_script_item)
                        setOnMenuItemClickListener { menuItem ->
                            itemListener.onItemMenuClick(menuItem, item)
                            false
                        }
                        setForceShowIcon(true)
                        show()
                    }
                }
            }
        }
    }

    object ScriptDiffUtilCallback : DiffUtil.ItemCallback<Scenario>() {
        override fun areItemsTheSame(
            oldItem: Scenario, newItem: Scenario
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Scenario, newItem: Scenario
        ): Boolean = oldItem == newItem
    }
}

interface ScriptItemCallback {
    fun onItemMenuClick(menuItem: MenuItem, item: Scenario)
    fun onStartScenario(item: Scenario)
}