package com.abidbe.sweetify.view.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentProfileBinding
import com.abidbe.sweetify.view.welcome.OnboardingActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            activity?.finish()
            return
        }

        refreshProfile()

        binding.logoutButton1.setOnClickListener {
            signOut()
        }

        binding.profileDetail.setOnClickListener {
            goToProfileDetail()
        }

        binding.historyButton.setOnClickListener {
            goToHistoryPurchase()
        }
    }

    private fun goToHistoryPurchase() {
        val intent = Intent(requireContext(), HistoryPurchaseActivity::class.java)
        startActivity(intent)
    }


    private fun refreshProfile() {
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            binding.tvname.text = firebaseUser.displayName
            firebaseUser.photoUrl?.let { photoUrl ->
                Glide.with(this)
                    .load(photoUrl)
                    .into(binding.photo)
            }
        }
    }

    private fun goToProfileDetail() {
        val intent = Intent(requireContext(), ProfileDetailActivity::class.java)
        profileDetailActivityResultLauncher.launch(intent)
    }

    private fun signOut() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setTitle(getString(R.string.sign_out))
            setMessage(getString(R.string.are_you_sure_you_want_to_sign_out))
            setPositiveButton("Yes") { dialog, which ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val credentialManager = CredentialManager.create(requireContext())
                    auth.signOut()
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    startActivity(Intent(requireContext(), OnboardingActivity::class.java))
                    activity?.finish()
                }
            }
            setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private val profileDetailActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshProfile()
        }
    }
}