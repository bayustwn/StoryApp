package com.example.storyapp.ui.activity.story

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.ui.viewmodel.StoryViewModel
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.StoryViewModelFactory
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import com.example.storyapp.utils.ResponseState

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailStoryBinding

    private val storyFactory = StoryViewModelFactory
    private val storyViewModel by viewModels<StoryViewModel> { storyFactory.getInstance(application) }
    private val preferencesFactory = UserPreferencesViewModelFactory
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }

    companion object{
        const val ID = "story_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id:String? = intent.getStringExtra(ID)
        if (id != null) {
            getDetail(id)
        }
    }

    private fun isLoading(loading: Boolean){
        if (loading){
            binding.apply {
                this.content.visibility = View.GONE
                this.loading.visibility = View.VISIBLE
            }
        }else{
            binding.apply {
                this.content.visibility = View.GONE
                this.loading.visibility = View.VISIBLE
            }
        }
    }

    private fun getDetail(id:String){
        preferencesVieModel.userToken.observe(this){token->
            if (token != null) {
                storyViewModel.getDetail("Bearer $token",id).apply {
                    storyViewModel.getDetail.observe(this@DetailStoryActivity){state->
                        when(state){
                            is ResponseState.Error -> {
                                isLoading(false)
                                Toast.makeText(applicationContext,state.message,Toast.LENGTH_SHORT).show()
                            }
                            is ResponseState.Success -> {
                                isLoading(false)
                                state.data.story?.let {
                                    binding.apply {
                                        this.tvDetailName.text = it.name
                                        Glide.with(applicationContext)
                                            .load(it.photoUrl)
                                            .into(binding.ivDetailPhoto)
                                        this.tvDetailDescription.text = it.description
                                        this.content.visibility = View.VISIBLE
                                        this.loading.visibility = View.GONE
                                    }

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
    }
}