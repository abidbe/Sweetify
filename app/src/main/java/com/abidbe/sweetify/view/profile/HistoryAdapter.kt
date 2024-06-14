package com.abidbe.sweetify.view.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.sweetify.data.local.WeeklySugarAmount
import com.abidbe.sweetify.databinding.ItemWeeklyBinding

class HistoryAdapter : ListAdapter<WeeklySugarAmount, HistoryAdapter.WordViewHolder>(WordsComparator()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordViewHolder {
        val  binding = ItemWeeklyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WordViewHolder(private val binding: ItemWeeklyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeeklySugarAmount) {
            binding.tvDate.text = data.date
            binding.tvAmountResult.text = data.totalSugarAmount.toString()
            if (data.totalSugarAmount < 50) {
                binding.cardBelowLimit1.visibility = View.VISIBLE
            } else {
                binding.cardExceedLimit1.visibility = View.VISIBLE
            }
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<WeeklySugarAmount>() {
        override fun areItemsTheSame(oldItem: WeeklySugarAmount, newItem: WeeklySugarAmount): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: WeeklySugarAmount, newItem: WeeklySugarAmount): Boolean {
            return oldItem.date == newItem.date
        }
    }
}