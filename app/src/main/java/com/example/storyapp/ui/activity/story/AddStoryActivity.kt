package com.example.storyapp.ui.activity.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.viewmodel.StoryViewModel
import com.example.storyapp.ui.viewmodel.UserPreferencesViewModel
import com.example.storyapp.ui.viewmodel.factory.StoryViewModelFactory
import com.example.storyapp.ui.viewmodel.factory.UserPreferencesViewModelFactory
import com.example.storyapp.utils.CompressImage.compressImage
import com.example.storyapp.utils.Dialog
import com.example.storyapp.utils.ResponseState
import com.example.storyapp.utils.WriteFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddStoryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityAddStoryBinding
    private val storyFactory = StoryViewModelFactory
    private val storyViewModel by viewModels<StoryViewModel> { storyFactory.getInstance(application) }
    private val preferencesFactory = UserPreferencesViewModelFactory
    private var isUploadClicked:Boolean = false
    private val preferencesVieModel by viewModels<UserPreferencesViewModel> {
        preferencesFactory.getInstance(
            application
        )
    }
    private lateinit var fusedLocation: FusedLocationProviderClient

    private val lauchPicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri?->
        if (uri != null){
            storyViewModel.postImage = uri
            binding.image.setImageURI(uri)
        }else{
            Toast.makeText(applicationContext,
                getString(R.string.no_image_selected),Toast.LENGTH_SHORT).show()
        }
    }

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            binding.location.isChecked = true
            fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        }else{
            Toast.makeText(this@AddStoryActivity,
                getString(R.string.lokasi_tidak_di_izinkan), Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted->
        if (isGranted){
            openCamera()
        }else{
            Dialog.messageDialog(supportFragmentManager,ContextCompat.getString(applicationContext,R.string.close),
                getString(
                    R.string.camera_permission_error
                ), isError = true)
        }
    }

    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSuccess->
        if (isSuccess){
            storyViewModel.postImage?.let {
                binding.image.setImageURI(it)
            }
        }else{
            Toast.makeText(applicationContext,getString(R.string.take_photo_failure),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.upload_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkLocationPermission()

        binding.gallery.setOnClickListener(this)
        binding.camera.setOnClickListener(this)

        binding.location.setOnCheckedChangeListener{_,check->
            if (check){
                checkLocationPermission()
            }
        }

        storyViewModel.postImage?.let {
            binding.image.setImageURI(it)
        }
    }

    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            binding.location.isChecked = true
            fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        }else{
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun cameraPermissionRequest(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            openCamera()
        }else{
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera(){
        storyViewModel.postImage = WriteFile.getImageUri(this)
        launchCamera.launch(storyViewModel.postImage!!)
    }

    private fun postStory(token: String,desc:String,lat:Float?=null,lon:Float?=null){
        val imageFile:File = storyViewModel.postImage?.let { WriteFile.uriToFile(it, applicationContext)?.compressImage() }!!
        val imageRequestBody: RequestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val latRequest = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequest = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        storyViewModel.postStory("Bearer $token",desc.toRequestBody("text/plain".toMediaType()),MultipartBody.Part.createFormData("photo",imageFile.name,imageRequestBody),latRequest,lonRequest).apply {
            storyViewModel.addResponse.observe(this@AddStoryActivity){event->
                event.getContentIfNotHandled()?.let { state->
                    when(state){
                        is ResponseState.Error -> {
                            isUploadClicked = false
                            isLoading(false)
                            Dialog.messageDialog(supportFragmentManager, isError = true, message = getString(
                                R.string.upload_error
                            ) +  state.message)
                        }
                        is ResponseState.Success -> {
                            isUploadClicked = false
                            isLoading(false)
                            Dialog.messageDialog(supportFragmentManager, isError = false, message = getString(
                                R.string.upload_success
                            )){
                                val intent = Intent(applicationContext,StoryActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
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

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation = LocationServices.getFusedLocationProviderClient(this)

            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude.toFloat()
                    val lon = location.longitude.toFloat()
                    post(lat,lon)
                } else {
                    Toast.makeText(this, getString(R.string.lokasi_tidak_ditemukan), Toast.LENGTH_SHORT).show()
                    isUploadClicked = false
                }
            }.addOnFailureListener {
                Toast.makeText(this, getString(R.string.gagal_mendapatkan_lokasi), Toast.LENGTH_SHORT).show()
                isUploadClicked = false
            }
        } else {
            checkLocationPermission()
        }
    }

    private fun post(lat: Float?,lon: Float?){
        isUploadClicked = true
        preferencesVieModel.userToken.observe(this@AddStoryActivity){token->
            if (storyViewModel.postImage == null || binding.edAddDescription.text.isNullOrEmpty()){
                Dialog.messageDialog(supportFragmentManager, close = getString(R.string.close), message = getString(R.string.form_error), isError = true)
            }else{
                val desc:String = binding.edAddDescription.text.toString()
                if (token != null) {
                    postStory(token,desc,lat, lon)
                }
            }
        }
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {
                this.camera.visibility = View.INVISIBLE
                this.gallery.visibility = View.INVISIBLE
                this.loading.visibility = View.VISIBLE
                this.location.visibility = View.INVISIBLE
            }
        }else{
            binding.apply {
                this.camera.visibility = View.VISIBLE
                this.gallery.visibility = View.VISIBLE
                this.loading.visibility = View.GONE
                this.location.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_story_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.button_add->{
                if (!isUploadClicked) {
                    isUploadClicked = true
                    if (binding.location.isChecked){
                        getLocation()
                    }else{
                        post(null,null)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.gallery->{
                lauchPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.camera->{
                cameraPermissionRequest()
            }
        }
    }
}