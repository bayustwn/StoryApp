package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.UserPreferencesRepository
import kotlinx.coroutines.launch

class UserPreferencesViewModel(private val userPreferencesRepository: UserPreferencesRepository): ViewModel() {

    val userToken: LiveData<String?> = userPreferencesRepository.getToken().asLiveData()

    fun saveToken(token:String){
        viewModelScope.launch {
            userPreferencesRepository.saveToken(token)
        }
    }

    fun clearToken(){
        viewModelScope.launch {
            userPreferencesRepository.clearToken()
        }
    }

}