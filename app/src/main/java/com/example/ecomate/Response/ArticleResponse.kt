package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("ArticleResponse")
	val articleResponse: List<ArticleResponseItem>
)

data class ArticleResponseItem(

	@field:SerializedName("articleLink")
	val articleLink: String,

	@field:SerializedName("raw_content")
	val rawContent: String,

	@field:SerializedName("Kode")
	val kode: Int,

	@field:SerializedName("author")
	val author: String,

	@field:SerializedName("date_created")
	val dateCreated: String,

	@field:SerializedName("clean_content")
	val cleanContent: String,

	@field:SerializedName("imageSrc")
	val imageSrc: String,

	@field:SerializedName("title")
	val title: String
)
