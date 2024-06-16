package com.abidbe.sweetify.view.glupedia

data class Glupedia(
    val id: Int,
    val title: String?,
    val content: String?,
    val photo: String?,
    val created_at: String?,
    val updated_at: String?
)

data class GlupediaListResponse(
    val data: List<Glupedia>
)

data class GlupediaResponse(
    val data: Glupedia
)