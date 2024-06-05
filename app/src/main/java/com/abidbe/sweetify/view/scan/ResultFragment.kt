package com.abidbe.sweetify.view.scan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentResultBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.uriToFile
import com.abidbe.sweetify.view.main.MainActivity
import kotlin.time.times

class ResultFragment : Fragment() {
    private lateinit var binding: FragmentResultBinding
    private val scanViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBuy.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        binding.buttonCancel.setOnClickListener {
            activity?.onBackPressed()
        }
        val imageUri = arguments?.getParcelable<Uri>(ARG_IMAGE_URI)
        val amount = arguments?.getDouble(ARG_AMOUNT)
        imageUri?.let {
            binding.imagePreview.setImageURI(it)
            analyzeImage(it, amount)
        }

    }

    private fun analyzeImage(image: Uri, amount: Double?) {
        val imageFile = uriToFile(image, requireContext())
        scanViewModel.uploadImage(imageFile)
        scanViewModel.scanResponse.observe(viewLifecycleOwner) { result ->
            result.getOrNull()?.let { scanResult ->
                binding.nameProduct.text = scanResult.product
                binding.tvGradeResult.text = scanResult.grade
                if (scanResult.grade == "A" || scanResult.grade == "B") {
                    binding.recommended.visibility = View.VISIBLE
                } else {
                    binding.notRecommended.visibility = View.VISIBLE
                }
                binding.tvSugarResult.text = scanResult.gulaSajianG
                val amountSugar = scanResult.gula100mlG
                amountSugar?.toDouble()
                val sugar = (amount?.div(100))?.times(amountSugar?.toDouble()!!)
                binding.tvAmountResult.text = sugar.toString()
            }
        }
        scanViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }


    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
    companion object {
        const val ARG_IMAGE_URI = "image_uri"
        const val ARG_AMOUNT = "amount"
    }
}