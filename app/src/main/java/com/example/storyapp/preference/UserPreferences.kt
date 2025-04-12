package com.example.storyapp.preference

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "UserToken")

class UserPreferences private constructor(private val context: Application){

    private val USER_TOKEN = stringPreferencesKey("user_token")

    suspend fun saveToken(token: String){
        context.dataStore.edit { pref->
            pref[USER_TOKEN] = token
        }
    }

    fun readToken(): Flow<String?>{
        return context.dataStore.data.map {
            pref -> pref[USER_TOKEN] ?: ""
        }
    }

    suspend fun clearToken(){
        context.dataStore.edit { it.clear() }
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferences?=null

        fun getInstance(context: Application): UserPreferences{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
        }
    }

}