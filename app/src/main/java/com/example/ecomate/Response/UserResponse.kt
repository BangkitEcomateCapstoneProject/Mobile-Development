package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("userList")
	val userList: List<UserListItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class UserListItem(

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("points")
	val points: Int
)
