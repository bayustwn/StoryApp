package com.example.storyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.ui.activity.auth.RegisterActivity
import com.example.storyapp.ui.activity.story.StoryActivity
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private val preferencesFactory = UserPreferencesViewModelFactory
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        lifecycleScope.launch {
            tokenvalidation()
        }

    }

    private suspend fun tokenvalidation(){
        delay(1000)
        preferencesVieModel.apply {
            preferencesVieModel.userToken.observe(this@SplashScreen){token->
                if (token.isNullOrBlank()){
                    goTo(RegisterActivity::class.java)
                }else{
                    goTo(StoryActivity::class.java)
                }
            }
        }
    }

    private fun goTo(activity: Class<*>){
        startActivity(Intent(applicationContext,activity))
        finish()
    }
}