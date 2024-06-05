package com.abidbe.sweetify.view.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.abidbe.sweetify.view.main.MainActivity
import com.abidbe.sweetify.R
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.databinding.FragmentResultAnalyzeBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.ResultState

class ResultAnalyzeFragment : DialogFragment() {
    private lateinit var binding: FragmentResultAnalyzeBinding
    private val scanViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    companion object {
        private const val ARG_PRODUCT = "product"
        private const val ARG_GRADE = "grade"
        private const val ARG_SUGAR = "sugar"
        private const val ARG_AMOUNT = "amount"

        fun newInstance(product: String, grade: String, sugar: Double, amount: Double): ResultAnalyzeFragment {
            val fragment = ResultAnalyzeFragment()
            val args = Bundle()
            args.putString(ARG_PRODUCT, product)
            args.putString(ARG_GRADE, grade)
            args.putString(ARG_SUGAR, sugar.toString())
            // Put other fields into args as needed
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBuy.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        scanViewModel.scanResponse.observe(viewLifecycleOwner) { result ->
            Log.d("ResultAnalyzeFragment", "Received scan result: $result")
            result.getOrNull()?.let {
                binding.nameProduct.text = it.product
                binding.tvGradeResult.text = it.grade
                if (it.grade == "A" || it.grade == "B") {
                    binding.recommended.visibility = View.VISIBLE
                } else {
                    binding.notRecommended.visibility = View.VISIBLE
                }
                binding.tvSugarResult.text = it.gulaSajianG
                binding.tvAmountResult.text = it.gula100mlG
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics
            ).toInt()
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
        }
    }
}
