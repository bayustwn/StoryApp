package com.example.storyapp.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.model.login.LoginRequest
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.activity.story.StoryActivity
import com.example.storyapp.ui.viewmodel.AuthViewModel
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.AuthViewModelFactory
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import com.example.storyapp.utils.Dialog
import com.example.storyapp.utils.ResponseState

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val authFactory = AuthViewModelFactory
    private val authViewModel by viewModels<AuthViewModel> { authFactory.getInstance(application) }
    private val preferencesFactory = UserPreferencesViewModelFactory
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }
    private lateinit var userData: LoginRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.registerButton.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)

    }

    private fun isFormEmpty(): Boolean {
        return binding.edLoginEmail.text.isNullOrBlank() && binding.edLoginPassword.text.isNullOrBlank()
    }

    private fun isValid(): Boolean {
        return binding.edLoginEmail.error.isNullOrBlank() && binding.edLoginPassword.error.isNullOrBlank()
    }

    private fun login() {
        authViewModel.login(userData).apply {
            authViewModel.loginResponse.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let{state->
                    when (state) {
                        is ResponseState.Error -> {
                            isLoading(false)
                            binding.loginButton.text = getString(R.string.login)
                            Dialog.messageDialog(
                                supportFragmentManager,
                                message = state.message,
                                isError = true
                            )
                        }
                        is ResponseState.Success -> {
                            isLoading(false)
                            binding.loginButton.text = getString(R.string.login)
                            Dialog.messageDialog(
                                supportFragmentManager,
                                message = state.data.message!!,
                                isError = false,
                                isOnCloselistener = {
                                    state.data.loginResult?.token?.let {
                                        preferencesVieModel.saveToken(
                                            it
                                        )
                                    }
                                    val intent = Intent(applicationContext, StoryActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                })
                        }
                        is ResponseState.loading -> {
                            isLoading(true)
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(loading: Boolean){
        if (loading){
            binding.loginButton.isEnabled = false
            binding.loading.alpha = 1f
            binding.loginButton.text = ""
        }else{
            binding.loginButton.isEnabled = true
            binding.loading.alpha = 0f
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.registerButton -> {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                finish()
            }
            binding.loginButton -> {
                userData = LoginRequest(
                    binding.edLoginEmail.text.toString(),
                    binding.edLoginPassword.text.toString()
                )
                if (isValid() && !isFormEmpty()) {
                    login()
                } else {
                    Dialog.messageDialog(
                        supportFragmentManager,
                        message = getString(R.string.form_error),
                        isError = true
                    )
                }
            }
        }
    }
}
