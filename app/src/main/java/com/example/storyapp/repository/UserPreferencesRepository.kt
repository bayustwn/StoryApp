package com.example.storyapp.repository

import com.example.storyapp.preference.UserPreferences
import kotlinx.coroutines.flow.Flow


class UserPreferencesRepository private constructor(private val userPreferences: UserPreferences) {

    suspend fun saveToken(token:String){
        userPreferences.saveToken(token)
    }

    fun getToken(): Flow<String?> = userPreferences.readToken()

    suspend fun clearToken(){
        userPreferences.clearToken()
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferencesRepository?=null

        fun getInstance(userPreferences: UserPreferences): UserPreferencesRepository{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: UserPreferencesRepository(userPreferences).also { INSTANCE = it }
            }
        }
    }

}