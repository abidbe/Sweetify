package com.abidbe.sweetify.view.history

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.databinding.ItemPurchaseBinding


class TrackerAdapter(
    private val context: Context,
    private val onDeleteClick: (Drink) -> Unit
) : ListAdapter<Drink, TrackerAdapter.WordViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemPurchaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding, context, onDeleteClick)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WordViewHolder(
        private val binding: ItemPurchaseBinding,
        private val context: Context,
        private val onDeleteClick: (Drink) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Drink) {
            binding.nameProduct.text = data.name
            binding.tvGradeResult.text = data.grade
            binding.tvAmountResult.text = data.sugarAmountBased.toString()

            binding.buttonDelete.setOnClickListener {
                showDeleteDialog(data)
            }
        }
        private fun showDeleteDialog(drink: Drink) {
            AlertDialog.Builder(context)
                .setTitle("Delete Drink")
                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes") { dialog, _ ->
                    onDeleteClick(drink)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    class WordsComparator : DiffUtil.ItemCallback<Drink>() {
        override fun areItemsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Drink, newItem: Drink): Boolean {
            return oldItem.name == newItem.name
        }
    }
}

