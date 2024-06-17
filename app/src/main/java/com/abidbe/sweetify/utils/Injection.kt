package com.abidbe.sweetify.utils

import android.content.Context
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.local.SweetifyDatabase
import com.abidbe.sweetify.data.repository.ScanRepository

object Injection {
    fun provideRepository(context: Context): ScanRepository {
        val apiService = ApiConfig.getApiService()
        val database = SweetifyDatabase.getDatabase(context)
        return ScanRepository.getInstance(apiService,database)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}