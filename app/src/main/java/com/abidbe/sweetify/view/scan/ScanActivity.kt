package com.abidbe.sweetify.view.scan

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abidbe.sweetify.R
import com.abidbe.sweetify.data.api.response.ApiConfig
import com.abidbe.sweetify.data.api.response.ScanResponse
import com.abidbe.sweetify.data.repository.ScanRepository
import com.abidbe.sweetify.databinding.ActivityScanBinding
import com.abidbe.sweetify.factory.ViewModelFactory
import com.abidbe.sweetify.utils.getImageUri
import com.abidbe.sweetify.utils.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Timeout
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import android.content.res.ColorStateList

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private var currentImageUri: Uri? = null
    private val scanViewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        triggerServerWithPlaceholder()
    }

    private fun triggerServerWithPlaceholder() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val placeholderFile = createPlaceholderFile(this@ScanActivity)
                    scanViewModel.uploadImage(placeholderFile)
                }

                scanViewModel.scanResponse.observe(this@ScanActivity) { result ->
                    if (result.isSuccess) {
                        binding.containerScanLoading.visibility = View.GONE
                        binding.containerScan.visibility = View.VISIBLE
                        binding.buttonContainer.visibility = View.GONE
                    } else {
                        Log.e("Server Error", "Failed to trigger server: ${result.exceptionOrNull()}")
                        binding.buttonContainer.visibility = View.VISIBLE
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("Timeout Error", "Connection timed out: ${e.message}")
                binding.buttonContainer.visibility = View.VISIBLE
            } catch (e: IOException) {
                Log.e("IO Error", "IOException: ${e.message}")
                // Handle other IOExceptions if necessary
                binding.buttonContainer.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("Error", "Unexpected error: ${e.message}")
                // Handle other unexpected errors
                binding.buttonContainer.visibility = View.VISIBLE
            }
        }
    }



    private fun createPlaceholderFile(context: Context): File {
        val file = File(context.cacheDir, "placeholder.jpg")
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawRect(0f, 0f, 100f, 100f, paint)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return file
    }


    private fun setupAction(){
        binding.buttonAnalyze.setOnClickListener {
            goToAnalyze(currentImageUri)
        }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonTry.setOnClickListener {
            binding.buttonContainer.visibility = View.GONE
            triggerServerWithPlaceholder()
        }
        binding.edLayoutSugar.helperText = getString(R.string.sugar_hint)
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
        if (imageUri == null && amountText.isEmpty()) {
            showErrorDialog("No image is selected", "Please select an image!")
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