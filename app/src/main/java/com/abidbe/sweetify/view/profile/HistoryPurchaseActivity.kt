package com.abidbe.sweetify.view.profile

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidbe.sweetify.databinding.ActivityHistoryPurchaseBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class HistoryPurchaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryPurchaseBinding
    private lateinit var auth: FirebaseAuth
    private val historyViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        setupView()
        observeWeeklyData()
        setupRecyclerView()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction(){
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val adapter = HistoryAdapter()
        binding.rvWeeklyHistory.layoutManager = LinearLayoutManager(this)
        binding.rvWeeklyHistory.adapter = adapter
        historyViewModel.weeklyHistory.observe(this) { weeklyHistory ->
            adapter.submitList(weeklyHistory)
        }
    }

    private fun observeWeeklyData() {
        auth = FirebaseAuth.getInstance()
        val startDate = getStartDate()
        val endDate = getEndDate()
        val firebaseUser = auth.currentUser
        lifecycleScope.launch {
            val userId = firebaseUser?.uid

            if (userId != null) {
                historyViewModel.loadWeeklyHistory(userId, startDate, endDate)
            }
        }
        binding.tvStartDate.text = startDate
        binding.tvEndDate.text = endDate

    }

    private fun getStartDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // Subtract 7 days from current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    private fun getEndDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }
}