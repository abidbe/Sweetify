package com.abidbe.sweetify.view.glupedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentGlupediaBinding
import com.abidbe.sweetify.view.main.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GlupediaFragment : Fragment() {
    private lateinit var binding: FragmentGlupediaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlupediaBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.btnTryAgain.setOnClickListener {
            fetchGlupedias()
        }
        fetchGlupedias()
        return binding.root
    }

    private fun fetchGlupedias() {
        showLoading(true)
        binding.btnTryAgain.visibility = View.GONE
        ApiClient.apiService.getAllGlupedias().enqueue(object : Callback<GlupediaListResponse> {
            override fun onResponse(call: Call<GlupediaListResponse>, response: Response<GlupediaListResponse>) {
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val glupedias = response.body()!!.data
                    Log.d("GlupediaFragment", "Fetched ${glupedias.size} glupedias")
                    binding.recyclerView.adapter = GlupediaAdapter(glupedias) { glupedia ->
                        // Navigate to detail fragment using NavController
                        val action = GlupediaFragmentDirections.actionGlupediaFragmentToGlupediaDetailFragment(glupedia.id)
                        findNavController().navigate(action)
                    }
                    binding.tvGlu.text = getString(R.string.learn_more_by_reading_the_article)
                    binding.ivGlu.setImageResource(R.drawable.newspaper)
                } else {
                    Log.e("GlupediaFragment", "Failed to fetch glupedias")
                    showRetryButton()
                }
            }

            override fun onFailure(call: Call<GlupediaListResponse>, t: Throwable) {
                showLoading(false)
                Log.e("GlupediaFragment", "Error fetching glupedias", t)
                showRetryButton()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showRetryButton() {
        binding.btnTryAgain.visibility = View.VISIBLE
    }
}
