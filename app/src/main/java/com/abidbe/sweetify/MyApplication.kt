package com.abidbe.sweetify

import android.app.Application
import com.abidbe.sweetify.data.local.SweetifyDatabase
import com.abidbe.sweetify.data.repository.ScanRepository

class SweetifyApplication : Application() {
    val database: SweetifyDatabase by lazy { SweetifyDatabase.getDatabase(this) }
}
