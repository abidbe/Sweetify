package com.abidbe.sweetify.view.profile

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.ActivityProfileDetailBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailBinding
    private lateinit var auth: FirebaseAuth
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUser()
        setupAction()
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.saveButton.setOnClickListener {
            updateUser()
        }

        binding.buttonEditPhoto.setOnClickListener {
            startGallery()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupUser() {
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        firebaseUser?.let {
            binding.edInputName.setText(it.displayName)
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.photo)
            binding.edInputEmail.setText(it.email)
        }
        binding.edLayoutEmail.helperText = getString(R.string.email_tooltip)
    }


    private fun updateUser() {
        showLoading()
        val username = binding.edInputName.text.toString()
        val firebaseUser = auth.currentUser
        firebaseUser?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .apply {
                    currentImageUri?.let { photoUri ->
                        setPhotoUri(photoUri)
                    }
                }
                .build()

            it.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        hideLoading()
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT)
                            .show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        hideLoading()
                        Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.photo)
        }
    }

    private fun showLoading() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.container.alpha = 0.5f
    }

    private fun hideLoading() {
        binding.progressIndicator.visibility = View.GONE
        binding.container.alpha = 1f
    }
}