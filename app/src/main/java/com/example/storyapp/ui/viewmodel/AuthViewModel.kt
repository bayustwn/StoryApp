package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.model.login.LoginRequest
import com.example.storyapp.model.login.LoginResponse
import com.example.storyapp.model.register.RegisterRequest
import com.example.storyapp.model.register.RegisterResponse
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.utils.EventHandler
import com.example.storyapp.utils.ResponseState
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {

    val registerResponse: LiveData<EventHandler<ResponseState<RegisterResponse>>> = authRepository.registerResponse
    val loginResponse :LiveData<EventHandler<ResponseState<LoginResponse>>> = authRepository.loginResponse

    fun register(userData: RegisterRequest) {
        viewModelScope.launch {
            authRepository.register(userData)
        }
    }

    fun login(userData: LoginRequest){
        viewModelScope.launch {
            authRepository.login(userData)
        }
    }

}