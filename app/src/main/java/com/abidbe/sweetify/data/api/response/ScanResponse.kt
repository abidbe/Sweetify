package com.abidbe.sweetify.data.api.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(

	@field:SerializedName("Gula/100ml(g)")
	val gula100mlG: String? = null,

	@field:SerializedName("Gula/Sajian(g)")
	val gulaSajianG: String? = null,

	@field:SerializedName("Product")
	val product: String? = null,

	@field:SerializedName("Grade")
	val grade: String? = null
)
