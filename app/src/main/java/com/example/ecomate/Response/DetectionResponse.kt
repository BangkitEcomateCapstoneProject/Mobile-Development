package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class DetectionResponse(

	@field:SerializedName("detectionList")
	val detectionList: List<DetectionListItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DetectionListItem(

	@field:SerializedName("probability")
	val probability: String,

	@field:SerializedName("created")
	val created: String,

	@field:SerializedName("prediction")
	val prediction: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("detectionId")
	val detectionId: Int
)
