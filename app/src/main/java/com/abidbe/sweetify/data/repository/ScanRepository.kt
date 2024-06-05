package com.abidbe.sweetify.data.repository

import com.abidbe.sweetify.data.api.ApiService
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.api.response.ScanResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class ScanRepository private constructor(
    val apiService: ApiService
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

    companion object {
        @Volatile
        private var instance: ScanRepository? = null
        fun getInstance(
            apiService: ApiService
        ): ScanRepository =
            instance ?: synchronized(this) {
                instance ?: ScanRepository(apiService)
            }.also { instance = it }
    }
}