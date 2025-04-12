package com.example.storyapp.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.R
import com.example.storyapp.model.story.AddStoryResponse
import com.example.storyapp.model.story.StoryResponse
import com.example.storyapp.api.ApiServices
import com.example.storyapp.model.error.ErrorResponse
import com.example.storyapp.model.story.ListStoryItem
import com.example.storyapp.model.story.StoryDetailResponse
import com.example.storyapp.model.story.StoryMapResponse
import com.example.storyapp.paging.StoryPagingSource
import com.example.storyapp.utils.EventHandler
import com.example.storyapp.utils.ResponseState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.UnknownHostException

class StoryRepository private constructor(private val apiServices: ApiServices,private val context: Application) {

    val addStoryResponse = MutableLiveData<EventHandler<ResponseState<AddStoryResponse>>>()
    val detailStories = MutableLiveData<ResponseState<StoryDetailResponse>>()
    val getAllLocation = MutableLiveData<ResponseState<StoryMapResponse>>()

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiServices,token)
            }
        ).liveData
    }

    suspend fun postStory(token: String,desc: RequestBody,photo: MultipartBody.Part,lat:RequestBody?,lon:RequestBody?){
        addStoryResponse.postValue(EventHandler(ResponseState.loading))
        try {
            val response = apiServices.postStories(token,lat,lon,desc,photo)
            addStoryResponse.postValue(EventHandler(ResponseState.Success(response)))
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            addStoryResponse.postValue(EventHandler(ResponseState.Error(errorBody.message.toString())))
        }catch (e: UnknownHostException){
            addStoryResponse.postValue(EventHandler(ResponseState.Error(context.getString(R.string.no_internet))))
        }
    }

    suspend fun getDetail(token:String,id:String){
        detailStories.postValue(ResponseState.loading)
        try {
            val response = apiServices.getStoriesDetail(id,token)
            detailStories.postValue(ResponseState.Success(response))
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            detailStories.postValue(ResponseState.Error(errorBody.message.toString()))
        }catch (e: UnknownHostException){
            detailStories.postValue(ResponseState.Error(context.getString(R.string.no_internet)))
        }
    }

    suspend fun getAllLocation(token: String){
        getAllLocation.postValue(ResponseState.loading)
        try {
            val response = apiServices.getAllLocation(token)
            getAllLocation.postValue(ResponseState.Success(response))
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            getAllLocation.postValue(ResponseState.Error(errorBody.message.toString()))
        }catch (e: UnknownHostException){
            getAllLocation.postValue(ResponseState.Error(context.getString(R.string.no_internet)))
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: StoryRepository?=null

        fun getInstance(apiServices: ApiServices,context: Application): StoryRepository{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: StoryRepository(apiServices,context).also { INSTANCE = it }
            }
        }
    }

}


















