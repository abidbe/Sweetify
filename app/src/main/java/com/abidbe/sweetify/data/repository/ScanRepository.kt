package com.abidbe.sweetify.data.repository

import com.abidbe.sweetify.data.api.ApiService
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.local.Drink
import com.abidbe.sweetify.data.local.SweetifyDao
import com.abidbe.sweetify.data.local.SweetifyDatabase
import com.abidbe.sweetify.data.local.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class ScanRepository private constructor(
    val apiService: ApiService,
    private val sweetifyDatabase: SweetifyDatabase
) {
    suspend fun uploadImage(imageFile: File): Result<ScanResponse> {
        return try {
            val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
            val multipartBody =
                MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name,
                    requestImageFile)
            val successResponse = apiService.uploadImage(multipartBody)
            Result.success(successResponse)
        } catch (e: HttpException){
            Result.failure(e)
        }
    }

    // Example method to save data to local database
    suspend fun saveUserToLocalDatabase(user: User) {
        // Perform database operation using sweetifyDao
        sweetifyDatabase.sweetifyDao().insertUser(user)
    }

    suspend fun saveDrinkToLocalDatabase(drink: Drink) {
        // Perform database operation using sweetifyDao
        sweetifyDatabase.sweetifyDao().insertDrink(drink)
    }

    // Example method to fetch data from local database
    suspend fun getDataFromLocalDatabase(userId: Int): List<Drink> {
        // Perform database operation using sweetifyDao
        return sweetifyDatabase.sweetifyDao().getDrinksByUserId(userId)
    }

    fun getDailyHistory(userId: Int, purchaseDate: String): Flow<List<Drink>> {
        return sweetifyDatabase.sweetifyDao().getDailyHistory(userId, purchaseDate)
    }

    fun getTotalSugarAmount(userId: Int, purchaseDate: String): Flow<Double?> {
        return sweetifyDatabase.sweetifyDao().getTotalSugarAmount(userId, purchaseDate)
    }

    fun getUserId(): Flow<Int?> {
        return sweetifyDatabase.sweetifyDao().getUserId()
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