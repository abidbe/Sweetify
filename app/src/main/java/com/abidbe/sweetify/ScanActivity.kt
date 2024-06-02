package com.abidbe.sweetify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.abidbe.sweetify.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAnalyze.setOnClickListener {
            showResultDialog()
        }
    }
    private fun showResultDialog() {
        val resultDialog: DialogFragment = ResultAnalyzeFragment()
        resultDialog.show(supportFragmentManager, "ResultDialog")
    }
}