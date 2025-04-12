package com.example.storyapp.api

import com.example.storyapp.model.story.AddStoryResponse
import com.example.storyapp.model.story.StoryResponse
import com.example.storyapp.model.login.LoginRequest
import com.example.storyapp.model.login.LoginResponse
import com.example.storyapp.model.register.RegisterRequest
import com.example.storyapp.model.register.RegisterResponse
import com.example.storyapp.model.story.StoryDetailResponse
import com.example.storyapp.model.story.StoryMapResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @POST("register")
    suspend fun register(@Body data: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body data:LoginRequest): LoginResponse

    @GET("stories")
    suspend fun getAllStories(@Header("Authorization") token:String, @Query("page") page:Int, @Query("size") size:Int ): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoriesDetail(@Path("id") id:String,@Header("Authorization") token:String): StoryDetailResponse

    @GET("stories?location=1")
    suspend fun getAllLocation(@Header("Authorization") token:String): StoryMapResponse

    @Multipart
    @POST("stories")
    suspend fun postStories(@Header("Authorization") token:String,@Part("lat") lat:RequestBody?, @Part("lon") lon:RequestBody? , @Part("description") desc: RequestBody, @Part photo: MultipartBody.Part): AddStoryResponse



}