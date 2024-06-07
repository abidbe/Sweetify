package com.abidbe.sweetify.view.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.repository.ScanRepository
import com.abidbe.sweetify.databinding.ActivityScanBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.getImageUri
import com.abidbe.sweetify.utils.uriToFile

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAnalyze.setOnClickListener {
            goToAnalyze(currentImageUri!!)
        }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun goToAnalyze(imageUri: Uri) {
        val amount = binding.edInputSugar.text.toString().toDouble()
        val fragment = ResultFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ResultFragment.ARG_IMAGE_URI, imageUri)
                putDouble(ResultFragment.ARG_AMOUNT, amount)
            }
        }
        val supportFragmentManager = supportFragmentManager
        val existingFragment = supportFragmentManager.findFragmentByTag(ResultFragment::class.java.simpleName)
        if (existingFragment !is ResultFragment) {
            supportFragmentManager.commit {
                add(binding.fragmentContainer.id, fragment, ResultFragment::class.java.simpleName)
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
            Log.d("Image URI", "showImage: $it")
            binding.imagePreview.setImageURI(it)
        }
    }
}