package com.abidbe.sweetify.view.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentHistoryBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPieChart()
    }

    private fun setPieChart() {
        //Chart with dummy data
        val pink200 = ContextCompat.getColor(requireContext(), R.color.pink_200)
        val pink400 = ContextCompat.getColor(requireContext(), R.color.pink_400)
        val pink50 = ContextCompat.getColor(requireContext(), R.color.pink_50)
        val pink950 = ContextCompat.getColor(requireContext(), R.color.pink_950)
        val pieChart = binding.pieChart

        // Data for the PieChart
        val entries = arrayListOf(
            PieEntry(30f, "Gram Consumed"),
            PieEntry(20f, "Available Gram"),
        )

        // Creating a PieDataSet
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

        // Creating PieData
        val data = PieData(dataSet)

        // Customizing the PieChart
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
        pieChart.legend.isEnabled = true
        pieChart.animateY(1400)
    }
}