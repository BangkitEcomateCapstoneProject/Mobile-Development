package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("probability")
	val probability: Any? = null,

	@field:SerializedName("prediction")
	val prediction: String? = null
)
