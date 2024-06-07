package com.abidbe.sweetify.view.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.data.repository.ScanRepository
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(private val scanRepository: ScanRepository) : ViewModel(){
    private val _dailyHistory = MutableLiveData<List<Drink>>()
    val dailyHistory: LiveData<List<Drink>> = _dailyHistory
    private val _pieData = MutableLiveData<List<PieEntry>>()
    val pieData: LiveData<List<PieEntry>> = _pieData
    private val _isLimit = MutableLiveData<Boolean>()
    val isLimit: LiveData<Boolean> = _isLimit

    private val dailyLimit = 50.0

    fun fetchDailyHistory(userId: Int, purchaseDate: String) {
        viewModelScope.launch {
            scanRepository.getDailyHistory(userId, purchaseDate).collect { drinks ->
                _dailyHistory.value = drinks
            }
        }
    }

    fun getUserId() = scanRepository.getUserId()

    fun fetchPieData(userId: Int, purchaseDate: String) {
        viewModelScope.launch {
            val totalSugarAmount = scanRepository.getTotalSugarAmount(userId, purchaseDate).first() ?: 0.0
            val availableSugarAmount = calculateAvailableSugar(totalSugarAmount)

            val pieEntries = if (totalSugarAmount > dailyLimit) {
                _isLimit.value = true
                listOf(
                    PieEntry(totalSugarAmount.toFloat(), "Gram Consumed")
                )
            } else {
                listOf(
                    PieEntry(totalSugarAmount.toFloat(), "Gram Consumed"),
                    PieEntry(availableSugarAmount.toFloat(), "Available Gram")
                )
            }

            _pieData.value = pieEntries
        }
    }

    private fun calculateAvailableSugar(totalSugar: Double): Double {
        return if (totalSugar >= dailyLimit) 0.0 else dailyLimit - totalSugar
    }


}