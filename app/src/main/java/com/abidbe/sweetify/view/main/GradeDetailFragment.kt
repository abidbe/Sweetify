package com.abidbe.sweetify.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.abidbe.sweetify.R
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

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        searchAdapter = SearchAdapter(listOf())
        binding.recyclerView.adapter = searchAdapter

        fetchProductsByGrade(grade)

        binding.btnTryAgain.setOnClickListener {
            fetchProductsByGrade(grade)
        }
    }

    private fun fetchProductsByGrade(grade: String) {
        showLoading(true)
        showTryAgainButton(false)
        val apiService = ApiClient.apiService
        apiService.getProductsByGrade(grade).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                showLoading(false)
                if (response.isSuccessful) {
                    searchAdapter.updateProducts(response.body() ?: listOf())
                    binding.tvGrade.text = grade
                    when (grade) {
                        "A" -> binding.tvGradeContent.text = getString(R.string.grade_content_a)
                        "B" -> binding.tvGradeContent.text = getString(R.string.grade_content_b)
                        "C" -> binding.tvGradeContent.text = getString(R.string.grade_content_c)
                        "D" -> binding.tvGradeContent.text = getString(R.string.grade_content_d)
                    }
                    binding.tvGradeContentTop.text = getString(R.string.grade_content)
                } else {
                    Log.e("GradeDetailFragment", "Failed to load products")
                    showTryAgainButton(true)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                showLoading(false)
                Log.e("GradeDetailFragment", "Error fetching products by grade", t)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
