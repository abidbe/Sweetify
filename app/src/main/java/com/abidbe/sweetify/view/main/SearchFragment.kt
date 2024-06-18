package com.abidbe.sweetify.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidbe.sweetify.databinding.FragmentSearchBinding
import com.abidbe.sweetify.view.main.api.ApiClient
import com.abidbe.sweetify.view.main.api.ApiService
import com.abidbe.sweetify.view.main.api.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var apiService: ApiService
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.apiService
        searchAdapter = SearchAdapter(listOf())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        // Load all products when fragment is created
        getProducts()

        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchProducts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        binding.btnTryAgain.setOnClickListener {
            getProducts()
        }
    }

    private fun getProducts() {
        showLoading(true)
        showTryAgainButton(false)
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        searchAdapter.updateProducts(it)
                    }
                    Log.d("SearchFragment", "Products loaded")
                } else {
                    Log.e("SearchFragment", "Failed to load products")
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                    showTryAgainButton(true)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                showLoading(false)
                Log.e("SearchFragment", "Failed to load products: ${t.message}")
                Toast.makeText(context, "Failed to load products: ${t.message}", Toast.LENGTH_SHORT).show()
                showTryAgainButton(true)
            }
        })
    }

    private fun searchProducts(query: String) {
        showLoading(true)
        showTryAgainButton(false)
        apiService.searchProducts(query).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        searchAdapter.updateProducts(it)
                    }
                    Log.d("SearchFragment", "Success")
                } else {
                    Log.e("SearchFragment", "Failed to fetch data")
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    showTryAgainButton(true)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                showLoading(false)
                Log.e("SearchFragment", "Failed to fetch data: ${t.message}")
                Toast.makeText(context, "Failed to fetch data: ${t.message}", Toast.LENGTH_SHORT).show()
                showTryAgainButton(true)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showTryAgainButton(show: Boolean) {
        binding.btnTryAgain.visibility = if (show) View.VISIBLE else View.GONE
    }
}
