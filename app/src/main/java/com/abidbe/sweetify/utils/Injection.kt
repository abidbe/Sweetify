package com.abidbe.sweetify.utils

import android.content.Context
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.repository.ScanRepository

object Injection {
    fun provideRepository(context: Context): ScanRepository {
        val apiService = ApiConfig.getApiService()
        return ScanRepository.getInstance(apiService)
    }
}