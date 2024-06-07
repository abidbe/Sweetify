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
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.data.local.User
import com.abidbe.sweetify.databinding.FragmentResultBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.uriToFile
import com.abidbe.sweetify.view.main.MainActivity
import java.util.Calendar
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
                binding.buttonBuy.setOnClickListener {
                    if (sugar != null) {
                        saveToLocal(scanResult, sugar)
                    }
                    goToMainActivity()
                }
            }
        }
        scanViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun saveToLocal(scanResponse: ScanResponse?, sugar: Double) {
        scanResponse?.let { response ->
            // Extract data from ScanResponse
            val product = response.product
            val sugarAmount = response.gulaSajianG
            val grade = response.grade

            // Assuming you have a user object available or a method to obtain it
            val user = User(uid = 1, username = "Salman")
            // Save user data to local database
            scanViewModel.saveUserToLocalDatabase(user)

            // Create a Drink object using the extracted data
            val drink = product?.let {
                sugarAmount?.let {it1 ->
                    Drink(
                        userId = user.uid, // Assuming you associate drinks with users based on their IDs
                        name = product, // Using product name as drink name
                        grade = grade!!, // Using first character of grade as drink grade
                        sugarAmountBased = sugar, // Converting sugar amount to double
                        purchaseDate = getCurrentDate() // Assuming purchase date is current time
                    )
                }
            }
            // Save drink data to local database
            if (drink != null) {
                scanViewModel.saveDrinkToLocalDatabase(drink)
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scanViewModel.scanResponse.removeObservers(viewLifecycleOwner)
        scanViewModel.isLoading.removeObservers(viewLifecycleOwner)
        activity?.finish()
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