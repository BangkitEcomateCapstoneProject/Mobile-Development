package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("Kode")
	val kode: Int? = null,

	@field:SerializedName("date_created")
	val dateCreated: String? = null,

	@field:SerializedName("author")
	val author: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null
)
