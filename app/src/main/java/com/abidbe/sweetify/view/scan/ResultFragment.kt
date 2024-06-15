package com.abidbe.sweetify.view.scan

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abidbe.sweetify.R
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.databinding.FragmentResultBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.uriToFile
import com.abidbe.sweetify.view.history.TrackerViewModel
import com.abidbe.sweetify.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val scanViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val trackerViewModel by viewModels<TrackerViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var availableSugar: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
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

        trackerViewModel.availableSugar.observe(viewLifecycleOwner) { available ->
            availableSugar = available
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val purchaseDate = getCurrentDate()
        userId?.let {
            trackerViewModel.fetchPieData(it, purchaseDate)
        }
    }

    private fun analyzeImage(image: Uri, amount: Double?) {
        val imageFile = uriToFile(image, requireContext())
        scanViewModel.uploadImage(imageFile)
        scanViewModel.scanResponse.observe(viewLifecycleOwner) { result ->
            result.getOrNull()?.let { scanResult ->
                binding.productName.text = scanResult.product
                binding.tvGradeResult.text = getString(
                    R.string.grade,
                    scanResult.grade
                )
                if (scanResult.grade == "A" || scanResult.grade == "B") {
                    binding.recommended.visibility = View.VISIBLE
                } else {
                    binding.notRecommended.visibility = View.VISIBLE
                }
                binding.tvSugarResult.text = scanResult.gulaSajianG
                val amountSugar = scanResult.gula100mlG
                amountSugar?.toDouble()
                val sugar = (amount?.div(100))?.times(amountSugar?.toDouble()!!)
                binding.tvSugarAmountResult.text = sugar.toString()
                binding.buttonBuy.setOnClickListener {
                    if (sugar != null) {
                        buyDrink(scanResult, sugar)
                    }
                }
            }
        }
        scanViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun buyDrink(scanResponse: ScanResponse?, sugar: Double) {
        scanResponse?.let {
            availableSugar?.let { available ->
                if (available == 0.0) {
                    showReassuranceDialog(scanResponse, sugar,
                        getString(R.string.no_available_sugar),
                        getString(R.string.message_nosugar))
                } else if (available < sugar) {
                    showReassuranceDialog(scanResponse, sugar,
                        getString(R.string.not_enough_available_sugar),
                        getString(R.string.message_notenoughsugar))
                } else {
                    saveToLocal(scanResponse, sugar)
                }
            }
        }
    }

    private fun showReassuranceDialog(scanResponse: ScanResponse, sugar: Double, title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                saveToLocal(scanResponse, sugar)
                goToMainActivity()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                goToMainActivity()
            }
            .create()
            .show()
    }

    private fun saveToLocal(scanResponse: ScanResponse?, sugar: Double) {
        scanResponse?.let { response ->
            val product = response.product
            val sugarAmount = response.gulaSajianG
            val grade = response.grade
            auth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            val drink = product?.let {
                sugarAmount?.let {
                    Drink(
                        userId = firebaseUser?.uid,
                        name = product,
                        grade = grade!!,
                        sugarAmountBased = sugar,
                        purchaseDate = getCurrentDate()
                    )
                }
            }
            if (drink != null) {
                scanViewModel.saveDrinkToLocalDatabase(drink)
                goToMainActivity()
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
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.mainLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    companion object {
        const val ARG_IMAGE_URI = "image_uri"
        const val ARG_AMOUNT = "amount"
    }
}

