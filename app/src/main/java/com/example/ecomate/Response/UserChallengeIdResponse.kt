package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class UserChallengeIdResponse(

	@field:SerializedName("challenge")
	val challenge: Challenge,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Challenge(

	@field:SerializedName("challengePoints")
	val challengePoints: Int,

	@field:SerializedName("challengeId")
	val challengeId: Int,

	@field:SerializedName("challengeDesc")
	val challengeDesc: String,

	@field:SerializedName("challengeTitle")
	val challengeTitle: String,

	@field:SerializedName("challengeStatus")
	val challengeStatus: String,

	@field:SerializedName("userId")
	val userId: String
)
