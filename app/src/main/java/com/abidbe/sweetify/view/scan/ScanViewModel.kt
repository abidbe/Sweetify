package com.abidbe.sweetify.view.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.data.repository.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ScanViewModel(private val scanRepository: ScanRepository): ViewModel() {
    private val _scanResponse = MutableLiveData<Result<ScanResponse>>()
    val scanResponse: LiveData<Result<ScanResponse>> = _scanResponse
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = scanRepository.uploadImage(imageFile)
            if (result.isSuccess) {
                _scanResponse.value = result
                _isLoading.value = false
            } else {
                _scanResponse.value = Result.failure(Exception("Failed to upload image"))
                _isLoading.value = false
            }
        }
    }

    fun saveDrinkToLocalDatabase(drink: Drink) {
        viewModelScope.launch(Dispatchers.Main) {
            scanRepository.saveDrinkToLocalDatabase(drink)
        }
    }
}
