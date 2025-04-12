package com.example.storyapp.ui.activity.auth


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.model.register.RegisterRequest
import com.example.storyapp.ui.viewmodel.AuthViewModel
import com.example.storyapp.ui.viewmodel.factory.AuthViewModelFactory
import com.example.storyapp.utils.Dialog
import com.example.storyapp.utils.ResponseState

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private val factory = AuthViewModelFactory
    private val authViewModel by viewModels<AuthViewModel> { factory.getInstance(application) }
    private lateinit var userData: RegisterRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)

    }

    private fun isFormEmpty(): Boolean{
        return binding.edRegisterName.text.isNullOrBlank() && binding.edRegisterEmail.text.isNullOrBlank() && binding.edRegisterPassword.text.isNullOrBlank()
    }

    private fun isValid(): Boolean{
        return binding.edRegisterEmail.error.isNullOrBlank() && binding.edRegisterName.error.isNullOrBlank() && binding.edRegisterPassword.error.isNullOrBlank()
    }

    private fun register(){
        authViewModel.register(userData).apply {
            authViewModel.registerResponse.observe(this@RegisterActivity){event->
                event.getContentIfNotHandled()?.let { state->
                    when(state){
                        is ResponseState.Error -> {
                            isLoading(false)
                            binding.registerButton.text = getString(R.string.register)
                            Dialog.messageDialog(supportFragmentManager, message = state.message, isError = true)
                        }
                        is ResponseState.Success -> {
                            isLoading(false)
                            binding.registerButton.text = getString(R.string.register)
                            Dialog.messageDialog(supportFragmentManager, close = getString(R.string.login),
                                getString(
                                    R.string.register_success
                                ),false) {
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            }
                        }
                        is ResponseState.loading -> {
                            isLoading(true)
                        }
                    }
                }

            }
        }
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.registerButton.isEnabled = false
            binding.registerButton.text = ""
            binding.loading.alpha = 1f
        }else{
            binding.loading.alpha = 0f
            binding.loading.visibility = View.GONE
            binding.registerButton.isEnabled = true
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.loginButton->{
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
            binding.registerButton->{
                userData = RegisterRequest(
                    binding.edRegisterName.text.toString(),
                    binding.edRegisterEmail.text.toString(),
                    binding.edRegisterPassword.text.toString()
                )
                if (isValid() && !isFormEmpty()){
                    register()
                }else{
                    Dialog.messageDialog(supportFragmentManager, message = getString(R.string.form_error), isError = true)
                }
            }
        }
    }
}