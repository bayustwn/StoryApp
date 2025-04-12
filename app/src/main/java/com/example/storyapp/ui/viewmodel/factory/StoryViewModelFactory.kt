package com.example.storyapp.ui.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.ui.viewmodel.StoryViewModel
import com.example.storyapp.utils.Injection

@Suppress("UNCHECKED_CAST")
class StoryViewModelFactory private constructor(private val storyRepository: StoryRepository): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)){
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException ("$modelClass not found")
    }

    companion object{
        @Volatile
        private var INSTANCE: StoryViewModelFactory?=null

        fun getInstance(context: Application): StoryViewModelFactory{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: StoryViewModelFactory(Injection.storyRepository(context)).also { INSTANCE = it }
            }
        }
    }

}