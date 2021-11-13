package com.example.registrationapp.retrofit

import com.example.registrationapp.model.JWT_token
import com.example.tokenapp.model.RefreshResponse
import com.example.tokenapp.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RestApi {

    @FormUrlEncoded
    @POST("register/")
    suspend fun sendUserData(
        @Field("number") number: Int,
        @Field("password") password: String,
        @Field("first_name") name: String,
        @Field("birthDate") birth: String
    ): Response<String>

    @FormUrlEncoded
    @POST("token/")
    suspend fun getJWTtoken(
        @Field("number") number: Int,
        @Field("password") uid: String
    ): Response<JWT_token>

    @FormUrlEncoded
    @POST("authorize/")
    suspend fun checkUserNumber(@Field("number") number: Int): Response<Boolean>

    @GET("user/")
    suspend fun getUserData(): Response<User>

    @FormUrlEncoded
    @POST("refresh/")
    fun updateAccessTokenWithRefresh(@Field("refresh") token: String): Call<RefreshResponse>

}