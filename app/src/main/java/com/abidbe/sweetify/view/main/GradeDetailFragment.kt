package com.abidbe.sweetify.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidbe.sweetify.databinding.FragmentGradeDetailBinding
import com.abidbe.sweetify.view.main.api.ApiClient
import com.abidbe.sweetify.view.main.api.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GradeDetailFragment : Fragment() {

    private var _binding: FragmentGradeDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGradeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val grade = arguments?.getString("GRADE") ?: "A"

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(listOf())
        binding.recyclerView.adapter = searchAdapter

        fetchProductsByGrade(grade)
    }

    private fun fetchProductsByGrade(grade: String) {
        val apiService = ApiClient.apiService
        apiService.getProductsByGrade(grade).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    searchAdapter.updateProducts(response.body() ?: listOf())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("GradeDetailFragment", "Error fetching products by grade", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
