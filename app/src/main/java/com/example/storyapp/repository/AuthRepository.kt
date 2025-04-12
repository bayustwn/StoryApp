package com.example.storyapp.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.R
import com.example.storyapp.api.ApiServices
import com.example.storyapp.model.error.ErrorResponse
import com.example.storyapp.model.login.LoginRequest
import com.example.storyapp.model.login.LoginResponse
import com.example.storyapp.model.register.RegisterRequest
import com.example.storyapp.model.register.RegisterResponse
import com.example.storyapp.utils.EventHandler
import com.example.storyapp.utils.ResponseState
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.UnknownHostException

class AuthRepository private constructor(private val apiServices: ApiServices,private val context: Application) {

    val registerResponse = MutableLiveData<EventHandler<ResponseState<RegisterResponse>>>()
    val loginResponse = MutableLiveData<EventHandler<ResponseState<LoginResponse>>>()

    suspend fun register(userData: RegisterRequest){
        registerResponse.postValue(EventHandler(ResponseState.loading))
        try {
            val response = apiServices.register(userData)
            registerResponse.postValue(EventHandler(ResponseState.Success(response)))
        }catch (e: HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            registerResponse.postValue(EventHandler(ResponseState.Error(errorBody.message.toString())))
        }catch (e: UnknownHostException){
            registerResponse.postValue(EventHandler(ResponseState.Error(context.getString(R.string.no_internet))))
        }
    }

    suspend fun login(userData: LoginRequest){
        loginResponse.postValue(EventHandler(ResponseState.loading))
        try {
            val response = apiServices.login(userData)
            loginResponse.postValue(EventHandler(ResponseState.Success(response)))
        }catch (e:HttpException){
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            loginResponse.postValue(EventHandler(ResponseState.Error(errorBody.message.toString())))
        }catch (e: UnknownHostException){
            loginResponse.postValue(EventHandler(ResponseState.Error(context.getString(R.string.no_internet))))
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: AuthRepository?=null

        fun getInstace(apiServices: ApiServices,application: Application): AuthRepository{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: AuthRepository(apiServices,application).also { INSTANCE = it }
            }
        }
    }

}