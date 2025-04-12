package com.example.storyapp.ui.activity.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityStoryBinding
import com.example.storyapp.model.story.ListStoryItem
import com.example.storyapp.model.story.Story
import com.example.storyapp.ui.activity.auth.LoginActivity
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.StoryAdapter
import com.example.storyapp.ui.maps.StoryMapsActivity
import com.example.storyapp.ui.viewmodel.StoryViewModel
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.StoryViewModelFactory
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import com.example.storyapp.utils.Dialog

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private val preferencesFactory = UserPreferencesViewModelFactory
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }
    private val storyFactory = StoryViewModelFactory
    private val storyViewModel by viewModels<StoryViewModel> { storyFactory.getInstance(application) }
    private val adapter: StoryAdapter by lazy { StoryAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.story)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferencesVieModel.userToken.observe(this) { token ->
            if (token != null) {
                getAllStory(token)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    private fun logout() {
        Dialog.confirmDialog(
            supportFragmentManager, getString(R.string.logout),
            getString(
                R.string.sure_logout
            ), R.drawable.logout
        ) {
            preferencesVieModel.clearToken()
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upload -> {
                startActivity(Intent(applicationContext, AddStoryActivity::class.java))
                true
            }

            R.id.action_logout -> {
                logout()
                true
            }

            R.id.maps -> {
                startActivity(Intent(applicationContext, StoryMapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun getAllStory(token: String) {
        binding.story.layoutManager = LinearLayoutManager(this)
        binding.story.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.getAllStory("Bearer $token").observe(this@StoryActivity) { data ->
            adapter.addLoadStateListener { loadState ->

                val isLoading = loadState.refresh is LoadState.Loading
                if (isLoading){
                    binding.error.apply {
                        this.visibility = View.GONE
                    }
                    binding.story.visibility =View.GONE
                    binding.loading.visibility = View.VISIBLE
                }else{
                    binding.loading.visibility = View.GONE
                }

                val isEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                if (isEmpty){
                    binding.error.apply {
                        this.visibility = View.VISIBLE
                        this.text = getString(R.string.story_not_exist)
                    }
                    binding.story.visibility =View.GONE
                }else{
                    binding.error.apply {
                        this.visibility = View.GONE
                    }
                    binding.story.visibility =View.VISIBLE
                }

                val isError = loadState.refresh as? LoadState.Error
                if (isError != null) {
                    binding.error.apply {
                        this.visibility = View.VISIBLE
                        this.text = isError.error.localizedMessage
                    }
                    binding.story.visibility =View.GONE
                } else {
                    binding.error.apply {
                        this.visibility = View.GONE
                    }
                    binding.story.visibility =View.VISIBLE
                }

            }
            adapter.submitData(lifecycle, data)
            adapter.onClick(object: StoryAdapter.OnItemClickListener{
                override fun onClick(id: String?) {
                    this@StoryActivity.startActivity(Intent(this@StoryActivity,DetailStoryActivity::class.java)
                        .putExtra(DetailStoryActivity.ID,id)
                    )
                }

            })
        }
    }
}
