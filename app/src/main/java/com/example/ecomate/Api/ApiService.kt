package com.example.ecomate.Api

import com.example.ecomate.Response.ArticleResponseItem
import com.example.ecomate.Response.ChallengeListResponseItem
import com.example.ecomate.Response.PredictResponse
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.Response.UserChallengeIdResponse
import com.example.ecomate.Response.UserChallengesResponse
import com.example.ecomate.Response.UserIdResponse
import com.example.ecomate.Response.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("trashbins/")
    fun getTrashHub(): Call<List<TrashHubResponseItem>>
    @GET("trashbins/search")
    fun getSearchTrashHub(
        @Query("q") query: String
    ): Call<List<TrashHubResponseItem>>

    @GET("/users")
    fun getUsers(): Call<UserResponse>

    @GET("/{userId}")
    fun getUserById(
        @Path("userId") userId: String
    ): Call<UserIdResponse>

    @POST("/storeUser")
    fun storeUser(
        @Body request: StoreUserRequest
    ): Call<UserIdResponse>

    @GET("/{userId}/challenges")
    fun getUserChallenges(
        @Path("userId") userId: String
    ): Call<UserChallengesResponse>

    @GET("/{userId}/challenges/{challengeId}")
    fun getChallengeDetail(
        @Path("userId") userId: String,
        @Path("challengeId") challengeId: Int
    ): UserChallengeIdResponse

    @POST("/{userId}/challenges")
    fun createChallenge(
        @Path("userId") userId: String,
        @Body request: ChallengeRequest
    ): Call<UserChallengeIdResponse>

    @GET("challengeslist")
    fun getChallengeList(): Call<List<ChallengeListResponseItem>>

    @PUT("/{userId}/challenges/{challengeId}/status")
    fun updateChallengeStatus(
        @Path("userId") userId: String,
        @Path("challengeId") challengeId: Int,
        @Body request: ChallengeStatusRequest
    ): Call<UserChallengeIdResponse>

    @POST("/{userId}/addPoints")
    fun addPointToUser(
        @Path("userId") userId: String,
        @Body request: AddPointRequest
    ): Call<UserIdResponse>

    //create Trash Detection todo
    //get data Trash Detection todo
}