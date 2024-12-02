package com.galixo.autoClicker.feature.config.ui.actions.savingLoading.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.galixo.autoClicker.core.scenarios.domain.model.Scenario
import com.galixo.autoClicker.feature.config.databinding.ItemLoadBinding
import com.galixo.autoClicker.feature.config.ui.actions.saveOrLoad.callback.ScriptItemCallback

class LoadListAdapter(private val itemListener: ScriptItemCallback) :
    ListAdapter<Scenario, RecyclerView.ViewHolder>(ScriptDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadBinding.inflate(inflater, parent, false)
        return ScriptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ScriptViewHolder).bind(getItem(position))


    inner class ScriptViewHolder(private val binding: ItemLoadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Scenario) {
            binding.apply {
                scriptName.text = item.name
                scriptMode.text = item.scenarioMode.name
                (layoutPosition + 1).apply { itemIndex.text = this.toString() }
                btnPlay.setOnClickListener { itemListener.onStartScenario(item) }

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