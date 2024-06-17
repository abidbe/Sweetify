package com.abidbe.sweetify.view.history

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentHistoryBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.Calendar


class TrackerFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private val trackerViewModel by viewModels<TrackerViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val handler = Handler(Looper.getMainLooper())
    private val updateDateRunnable = object : Runnable {
        override fun run() {
            binding.tvDate.text = getCurrentDate()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val entries = arrayListOf(
            PieEntry(0f, "Gram Consumed"),
            PieEntry(50f, "Available Gram"),
        )
        setPieChart(entries)
        setupRecyclerView()
        observeHistoryData()
        observePieData()
    }
    
    private fun observePieData() {
        lifecycleScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val date = getCurrentDate()
            trackerViewModel.fetchPieData(userId, date)
            trackerViewModel.fetchTotalSugarAmount(userId, date)
        }
        trackerViewModel.totalSugarAmount.observe(viewLifecycleOwner) { totalSugarAmount ->
            binding.tvSugarValue.text = totalSugarAmount.toString()
            val availableSugarAmount = trackerViewModel.calculateAvailableSugar(totalSugarAmount)
            binding.tvSugarValueAvailable.text = availableSugarAmount.toString()
        }
        trackerViewModel.pieData.observe(viewLifecycleOwner) { pieEntries ->
            trackerViewModel.isLimit.observe(viewLifecycleOwner) { isLimit ->
                if (isLimit) {
                    binding.cardExceedLimit.visibility = View.VISIBLE
                } else {
                    binding.cardBelowLimit.visibility = View.VISIBLE
                }
            }
            setPieChart(pieEntries)
            binding.rvHistory.visibility = View.VISIBLE
        }

        trackerViewModel.isNoData.observe(viewLifecycleOwner) { isNoData ->
            if (isNoData) {
                binding.cardNoData.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            }
        }
    }

    private fun setPieChart(entries: List<PieEntry>) {
        val pink300 = ContextCompat.getColor(requireContext(), R.color.pink_300)
        val pink500 = ContextCompat.getColor(requireContext(), R.color.pink_500)
        val pink200 = ContextCompat.getColor(requireContext(), R.color.pink_200)
        val pink950 = ContextCompat.getColor(requireContext(), R.color.pink_950)
        val pieChart = binding.pieChart

        val dataSet = PieDataSet(entries, "Sugar Consumption")
        val colors = arrayListOf(
            pink500,
            pink300,
        )
        dataSet.colors = colors
        dataSet.valueTextColor = pink950
        dataSet.valueTextSize = 12f
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 70f
        pieChart.setHoleColor(pink200)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.setEntryLabelColor(pink950)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.legend.isEnabled = false
        pieChart.animateY(800)
        pieChart.isRotationEnabled = false
        pieChart.setDrawSliceText(false)
    }

    private fun setupRecyclerView() {
        val adapter = TrackerAdapter(requireContext()) { drink ->
            trackerViewModel.deleteDrink(drink)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        trackerViewModel.dailyHistory.observe(viewLifecycleOwner) { history ->
            adapter.submitList(history)
            observePieData()
        }
        trackerViewModel.pieData.observe(viewLifecycleOwner) { pieEntries ->
            setPieChart(pieEntries)
        }
    }


    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }


    private fun observeHistoryData() {
        lifecycleScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val date = getCurrentDate()
            trackerViewModel.fetchDailyHistory(userId, date)
        }
        trackerViewModel.dailyHistory.observe(viewLifecycleOwner) { history ->
            (binding.rvHistory.adapter as? TrackerAdapter)?.submitList(history)
        }
        trackerViewModel.pieData.observe(viewLifecycleOwner) { pieEntries ->
            setPieChart(pieEntries)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateDateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateDateRunnable)
    }
}