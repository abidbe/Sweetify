package com.abidbe.sweetify.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abidbe.sweetify.data.local.WeeklySugarAmount
import com.abidbe.sweetify.data.repository.ScanRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val scanRepository: ScanRepository): ViewModel() {
    private val _weeklyHistory = MutableLiveData<List<WeeklySugarAmount>>(emptyList())
    val weeklyHistory: LiveData<List<WeeklySugarAmount>> = _weeklyHistory

    fun loadWeeklyHistory(userId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            scanRepository.getWeeklyHistory(userId, startDate, endDate).collect { history ->
                _weeklyHistory.value = history
            }
        }
    }
}