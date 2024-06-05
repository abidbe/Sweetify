package com.abidbe.sweetify.data.api

import com.abidbe.sweetify.data.api.response.ScanResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): ScanResponse
}