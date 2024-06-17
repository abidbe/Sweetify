package com.abidbe.sweetify.data.repository

import com.abidbe.sweetify.data.api.ApiService
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.data.local.SweetifyDatabase
import com.abidbe.sweetify.data.local.WeeklySugarAmount
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class ScanRepository private constructor(
    val apiService: ApiService,
    private val sweetifyDatabase: SweetifyDatabase
) {
    suspend fun uploadImage(imageFile: File?): Result<ScanResponse> {
        return try {
            val requestImageFile = imageFile?.asRequestBody("image/jpg".toMediaType())
            val multipartBody =
                requestImageFile?.let {
                    MultipartBody.Part.createFormData(
                        "file",
                        imageFile.name,
                        it
                    )
                } ?: MultipartBody.Part.createFormData("file", "placeholder.jpg", ByteArray(0).toRequestBody("image/jpg".toMediaType()))
            val successResponse = apiService.uploadImage(multipartBody)
            Result.success(successResponse)
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }



    suspend fun saveDrinkToLocalDatabase(drink: Drink) {
        sweetifyDatabase.sweetifyDao().insertDrink(drink)
    }

    fun getDailyHistory(userId: String, purchaseDate: String): Flow<List<Drink>> {
        return sweetifyDatabase.sweetifyDao().getDailyHistory(userId, purchaseDate)
    }

    fun getTotalSugarAmount(userId: String, purchaseDate: String): Flow<Double?> {
        return sweetifyDatabase.sweetifyDao().getTotalSugarAmount(userId, purchaseDate)
    }

    suspend fun deleteDrink(drink: Drink) {
        sweetifyDatabase.sweetifyDao().deleteDrink(drink)
    }

    fun getWeeklyHistory(userId: String, startDate: String, endDate: String): Flow<List<WeeklySugarAmount>> {
        return sweetifyDatabase.sweetifyDao().getDailySugarAmounts(userId, startDate, endDate)
    }

    companion object {
        @Volatile
        private var instance: ScanRepository? = null
        fun getInstance(
            apiService: ApiService,
            sweetifyDatabase: SweetifyDatabase
        ): ScanRepository =
            instance ?: synchronized(this) {
                instance ?: ScanRepository(apiService, sweetifyDatabase)
            }.also { instance = it }
    }
}