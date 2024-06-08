package com.abidbe.sweetify.view.welcome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.sweetify.R

class OnboardingAdapter(private val pages: List<OnboardingPage>) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val page = pages[position]
        holder.imageView.setImageResource(page.imageResId)
        holder.titleView.text = page.title
        holder.descriptionView.text = page.description
    }

    override fun getItemCount(): Int = pages.size

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_onboarding)
        val titleView: TextView = itemView.findViewById(R.id.text_onboarding_title)
        val descriptionView: TextView = itemView.findViewById(R.id.text_onboarding_description)
    }
}

data class OnboardingPage(val imageResId: Int, val title: String, val description: String)