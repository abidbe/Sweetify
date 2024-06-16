package com.abidbe.sweetify.view.main.api

import com.abidbe.sweetify.view.glupedia.Glupedia
import com.abidbe.sweetify.view.glupedia.GlupediaListResponse
import com.abidbe.sweetify.view.glupedia.GlupediaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("produk")
    fun getProducts(): Call<List<Product>>

    @GET("produk/search")
    fun searchProducts(@Query("query") query: String): Call<List<Product>>

    @GET("produk/search")
    fun getProductsByGrade(@Query("grade") grade: String): Call<List<Product>>

    @GET("glupedias")
    fun getAllGlupedias(): Call<GlupediaListResponse>

    @GET("glupedias/{id}")
    fun getGlupediaById(@Path("id") id: Int): Call<GlupediaResponse>
}