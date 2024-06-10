package com.abidbe.sweetify.view.scan

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
            goToAnalyze(currentImageUri)
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

    private fun goToAnalyze(imageUri: Uri?) {
        val amountText = binding.edInputSugar.text.toString()
        if (imageUri == null || amountText.isEmpty()) {
            showErrorDialog("No image selected", "Please select an image!")
            return
        }
        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            showErrorDialog("Invalid amount", "Please enter a valid amount!")
            return
        }
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

    private fun showErrorDialog(title: String, message: String) {
        val errorDialog = ErrorDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
                putString("message", message)
            }
        }
        errorDialog.show(supportFragmentManager, ErrorDialogFragment.TAG)
    }


    class ErrorDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val title = arguments?.getString("title") ?: "Error"
            val message = arguments?.getString("message") ?: "An unexpected error occurred."

            return AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
        }

        companion object {
            const val TAG = "ErrorDialogFragment"
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
            binding.imgPreview.setImageURI(it)
            binding.edInputSugar.isEnabled = true
            binding.edLayoutSugar.isHelperTextEnabled = false
            binding.buttonGallery.visibility = View.GONE
        }
    }

}