package com.abidbe.sweetify.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abidbe.sweetify.databinding.ItemProductBinding
import com.abidbe.sweetify.view.main.api.Product

class SearchAdapter(private var products: List<Product>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductName.text = product.produk
            binding.tvProductGrade.text = "Grade: ${product.grade}"
            binding.tvProductSugar.text = "Sugar: ${product.gula}g"
            binding.tvProductServing.text = "Serving Size: ${product.takaran}ml"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}