package com.example.storyapp.ui.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.ui.viewmodel.AuthViewModel
import com.example.storyapp.utils.Injection

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory private constructor(private val authRepository: AuthRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException ("$modelClass Do Not Exist")
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthViewModelFactory?=null

        fun getInstance(context: Application): AuthViewModelFactory {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: AuthViewModelFactory(Injection.authRepositoty(context)).also { INSTANCE = it }
            }
        }
    }

}