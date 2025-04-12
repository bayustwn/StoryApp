package com.example.storyapp.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.example.storyapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityStoryMapsBinding
import com.example.storyapp.ui.viewmodel.StoryViewModel
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.StoryViewModelFactory
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import com.example.storyapp.utils.Dialog
import com.example.storyapp.utils.ResponseState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.MapStyleOptions

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val preferencesFactory = UserPreferencesViewModelFactory
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }
    private val storyFactory = StoryViewModelFactory
    private val storyViewModel by viewModels<StoryViewModel> { storyFactory.getInstance(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        preferencesVieModel.userToken.observe(this){token->
            if (token != null) {
                getAllLocation(token)
            }
        }

    }

    private fun getAllLocation(token:String){
        storyViewModel.getAllLocation(token).apply {
            storyViewModel.getAllLocation.observe(this@StoryMapsActivity){response->
                when(response){
                    is ResponseState.loading -> {

                    }
                    is ResponseState.Success -> {
                        if (response.data.listStories?.isNotEmpty() == true){

                            response.data.listStories[0].let { loc->
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.lat,loc.lon), 15f))
                            }

                            response.data.listStories.forEach { story->
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(story.lat,story.lon))
                                        .title(story.name)
                                        .snippet(story.description)
                                )

                            }
                        }
                    }
                    is ResponseState.Error -> {
                        Dialog.messageDialog(supportFragmentManager, isError = true, message = "Location data error")
                    }
                }
            }
        }
    }
}