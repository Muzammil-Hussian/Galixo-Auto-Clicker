package com.galixo.autoClicker.modules.language.presentation.inAppLanguage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.galixo.autoClicker.databinding.ItemLanguageBinding
import com.galixo.autoClicker.modules.language.domain.entities.ItemLanguage

class AdapterInAppLanguage(
    private val onLanguageSelected: (String) -> Unit
) : ListAdapter<ItemLanguage, AdapterInAppLanguage.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, onLanguageSelected)
    }

    inner class CustomViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemLanguage, onClick: (String) -> Unit) {
            binding.apply {
                languageName.text = item.languageName
                languageTranslatedName.text = item.languageNameTranslated
                rbSelected.isChecked = item.isSelected
                root.setOnClickListener { onClick(item.languageCode) }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ItemLanguage>() {
        override fun areItemsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage) = oldItem.languageCode == newItem.languageCode
        override fun areContentsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage) = oldItem == newItem
    }
}


