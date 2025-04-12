package com.example.storyapp.ui.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.repository.UserPreferencesRepository
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.utils.Injection

@Suppress("UNCHECKED_CAST")
class UserPreferencesViewModelFactory private constructor(private val userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPreferencesViewModel::class.java)){
            return UserPreferencesViewModel(userPreferencesRepository) as T
        }

        throw IllegalArgumentException ("$modelClass not found")
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferencesViewModelFactory?=null

        fun getInstance(context: Application): UserPreferencesViewModelFactory{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: UserPreferencesViewModelFactory(Injection.preferencesRepository(context)).also { INSTANCE = it }
            }
        }
    }

}