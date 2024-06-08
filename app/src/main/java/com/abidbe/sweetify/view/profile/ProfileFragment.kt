package com.abidbe.sweetify.view.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.abidbe.sweetify.databinding.FragmentProfileBinding
import com.abidbe.sweetify.view.login.LoginActivity
import com.abidbe.sweetify.view.welcome.OnboardingActivity
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            activity?.finish()
            return
        }

        // Display user's profile information
        binding.tvname.text = firebaseUser.displayName
        binding.tvemail.text = firebaseUser.email

        firebaseUser.photoUrl?.let { photoUrl ->
            // Use Glide to load the profile image
            Glide.with(this)
                .load(photoUrl)
                .into(binding.photo)
        }

        // Sign out button
        binding.logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        viewLifecycleOwner.lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            activity?.finish()
        }
    }
}