package com.abidbe.sweetify.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abidbe.sweetify.data.repository.ScanRepository
import com.abidbe.sweetify.utils.Injection
import com.abidbe.sweetify.utils.SettingPreferences
import com.abidbe.sweetify.utils.dataStore
import com.abidbe.sweetify.view.history.TrackerViewModel
import com.abidbe.sweetify.view.profile.HistoryViewModel
import com.abidbe.sweetify.view.profile.SettingViewModel
import com.abidbe.sweetify.view.scan.ScanViewModel

class ViewModelFactory(
    private val scanRepository: ScanRepository,
    private val settingPreferences: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(scanRepository) as T
            }

            modelClass.isAssignableFrom(TrackerViewModel::class.java) -> {
                TrackerViewModel(scanRepository) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(scanRepository) as T
            }

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(settingPreferences) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                        Injection.provideSettingPreferences(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}