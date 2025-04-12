package com.example.storyapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.model.story.AddStoryResponse
import com.example.storyapp.model.story.ListStoryItem
import com.example.storyapp.model.story.StoryDetailResponse
import com.example.storyapp.model.story.StoryMapResponse
import com.example.storyapp.model.story.StoryResponse
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.utils.EventHandler
import com.example.storyapp.utils.ResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    val addResponse: LiveData<EventHandler<ResponseState<AddStoryResponse>>> = storyRepository.addStoryResponse
    val getDetail: LiveData<ResponseState<StoryDetailResponse>> = storyRepository.detailStories
    val getAllLocation: LiveData<ResponseState<StoryMapResponse>> = storyRepository.getAllLocation
    var postImage: Uri?=null

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getAllStory(token).cachedIn(viewModelScope)
    }

    fun getAllLocation(token: String){
        viewModelScope.launch {
            storyRepository.getAllLocation("Bearer $token")
        }
    }

    fun postStory(token: String,desc: RequestBody,photo: MultipartBody.Part,lat:RequestBody?,lon:RequestBody?){
        viewModelScope.launch {
            storyRepository.postStory(token,desc,photo,lat,lon)
        }
    }

    fun getDetail(token: String,id:String){
        viewModelScope.launch {
            storyRepository.getDetail(token,id)
        }
    }


}