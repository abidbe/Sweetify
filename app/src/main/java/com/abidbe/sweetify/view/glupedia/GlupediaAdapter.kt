package com.abidbe.sweetify.view.glupedia

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.sweetify.databinding.ItemGlupediaBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GlupediaAdapter(private val glupedias: List<Glupedia>, private val listener: (Glupedia) -> Unit) : RecyclerView.Adapter<GlupediaAdapter.GlupediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlupediaViewHolder {
        val binding = ItemGlupediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GlupediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GlupediaViewHolder, position: Int) {
        holder.bind(glupedias[position], listener)
    }

    override fun getItemCount() = glupedias.size

    class GlupediaViewHolder(private val binding: ItemGlupediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(glupedia: Glupedia, listener: (Glupedia) -> Unit) {
            binding.titleTextView.text = glupedia.title
            binding.dateTextView.text = formatDate(glupedia.created_at ?: "")
            Glide.with(binding.root.context).load(glupedia.photo).into(binding.imageView)
            binding.root.setOnClickListener { listener(glupedia) }
        }

        private fun formatDate(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            return when {
                DateUtils.isToday(date.time) -> "Today"
                DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
                else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}