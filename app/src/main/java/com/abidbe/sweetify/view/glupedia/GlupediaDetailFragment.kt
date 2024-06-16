package com.abidbe.sweetify.view.glupedia

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentGlupediaDetailBinding
import com.abidbe.sweetify.view.main.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class GlupediaDetailFragment : Fragment() {
    private lateinit var binding: FragmentGlupediaDetailBinding
    private val args: GlupediaDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlupediaDetailBinding.inflate(inflater, container, false)
        fetchGlupediaDetails(args.glupediaId)
        return binding.root
    }

    private fun fetchGlupediaDetails(id: Int) {
        showLoading(true)
        ApiClient.apiService.getGlupediaById(id).enqueue(object : Callback<GlupediaResponse> {
            override fun onResponse(call: Call<GlupediaResponse>, response: Response<GlupediaResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.data?.let { glupedia ->
                        Log.d("GlupediaDetailFragment", "Glupedia data: $glupedia")
                        binding.detailTitleTextView.text = glupedia.title ?: getString(R.string.no_title)
                        binding.detailDateTextView.text = formatDate(glupedia.created_at)
                        binding.detailContentTextView.text = glupedia.content ?: getString(R.string.no_content)
                        glupedia.photo?.takeIf { it.isNotBlank() }?.let { photoUrl ->
                            Glide.with(this@GlupediaDetailFragment)
                                .load(photoUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .into(binding.detailImageView)
                        } ?: run {
                            Log.w("GlupediaDetailFragment", "Photo URL is blank or null")
                            binding.detailImageView.setImageResource(R.drawable.placeholder_image)
                        }
                    } ?: run {
                        Log.e("GlupediaDetailFragment", "Glupedia data is null")
                        showError("Failed to load data")
                    }
                } else {
                    Log.e("GlupediaDetailFragment", "Failed to fetch glupedia details: ${response.errorBody()?.string()}")
                    showError("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<GlupediaResponse>, t: Throwable) {
                showLoading(false)
                Log.e("GlupediaDetailFragment", "Error fetching glupedia details", t)
                showError("Network error, please try again")
            }
        })
    }

    private fun formatDate(dateString: String?): String {
        return try {
            dateString?.let {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(it)
                return when {
                    DateUtils.isToday(date.time) -> "Today"
                    DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
                    else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
                }
            } ?: ""
        } catch (e: Exception) {
            Log.e("GlupediaDetailFragment", "Error parsing date", e)
            ""
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        // Show error message to the user, for example using a Toast or a Snackbar
    }
}