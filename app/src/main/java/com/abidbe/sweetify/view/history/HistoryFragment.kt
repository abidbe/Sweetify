package com.abidbe.sweetify.view.history

import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
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
            val userId = historyViewModel.getUserId().first() ?: return@launch
            val date = getCurrentDate()
            historyViewModel.fetchPieData(userId, date)
        }

        historyViewModel.pieData.observe(viewLifecycleOwner) { pieEntries ->
            if (pieEntries.isEmpty() || pieEntries == null){
                binding.cardNoData.visibility = View.VISIBLE
            }else{
                historyViewModel.isLimit.observe(viewLifecycleOwner){ isLimit ->
                    if (isLimit){
                        binding.cardExceedLimit.visibility= View.VISIBLE
                    }
                }
                setPieChart(pieEntries)
                binding.cardNoData.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun setPieChart(entries: List<PieEntry>) {
        val pink200 = ContextCompat.getColor(requireContext(), R.color.pink_200)
        val pink400 = ContextCompat.getColor(requireContext(), R.color.pink_400)
        val pink50 = ContextCompat.getColor(requireContext(), R.color.pink_50)
        val pink950 = ContextCompat.getColor(requireContext(), R.color.pink_950)
        val pieChart = binding.pieChart

        val dataSet = PieDataSet(entries, "Sugar Consumption")
        val colors = arrayListOf(
            pink400,
            pink200,
        )
        dataSet.colors = colors
        dataSet.valueTextColor = pink950
        dataSet.valueTextSize = 12f
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 35f
        pieChart.setHoleColor(pink50)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(false)
        pieChart.setEntryLabelColor(pink950)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.legend.isEnabled = false
        pieChart.animateY(800)
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = HistoryAdapter()
    }


    private fun getCurrentDate(): String {
        // Use Calendar to get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is zero-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Format the date as needed (e.g., "yyyy-MM-dd")
        return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
    }

    private fun observeHistoryData() {
        lifecycleScope.launch {
            val userId = historyViewModel.getUserId().first() ?: return@launch
            val date = getCurrentDate()
            val exampleData = System.currentTimeMillis()
            Log.d("HistoryFragment", "User ID: $userId, Date: $exampleData")
            binding.tvDate.text = date
            historyViewModel.fetchDailyHistory(userId, date)
        }
        historyViewModel.dailyHistory.observe(viewLifecycleOwner) { history ->
            (binding.rvHistory.adapter as? HistoryAdapter)?.submitList(history)
        }
    }
}