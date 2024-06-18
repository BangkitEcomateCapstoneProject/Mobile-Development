package com.example.ecomate.Api

import com.example.ecomate.Response.ArticleResponseItem
import com.example.ecomate.Response.PredictResponse
import com.example.ecomate.Response.TrashHubResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("predict")
    suspend fun Predict(
        @Part file: MultipartBody.Part
    ):PredictResponse

    @POST("recommend")
    fun ArticleRecomend(
        @Body request: ArticleRequest
    ): Call<List<ArticleResponseItem>>
    @GET("trashHub")
    fun getTrashHub(): Call<TrashHubResponse>
    @GET("search")
    fun getSearchTrashHub(

    ): Call<TrashHubResponse>

}