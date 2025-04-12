package com.example.storyapp.utils

import android.app.Application
import com.example.storyapp.api.ApiClient
import com.example.storyapp.preference.UserPreferences
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserPreferencesRepository

object Injection {

    fun authRepositoty(context: Application): AuthRepository {
        val apiServices = ApiClient.apiServices
        return AuthRepository.getInstace(apiServices,context)
    }

    fun preferencesRepository(context: Application): UserPreferencesRepository {
        val userPreferences = UserPreferences.getInstance(context)
        return UserPreferencesRepository.getInstance(userPreferences)
    }

    fun storyRepository(context: Application): StoryRepository{
        val apiServices = ApiClient.apiServices
        return StoryRepository.getInstance(apiServices,context)
    }



}