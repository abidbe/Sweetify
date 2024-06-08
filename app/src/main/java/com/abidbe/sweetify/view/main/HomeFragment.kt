package com.abidbe.sweetify.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abidbe.sweetify.databinding.FragmentHomeBinding
import com.abidbe.sweetify.view.welcome.OnboardingActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Onboarding activity
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            activity?.finish()
            return
        }
        val displayName = firebaseUser.displayName
        // Display user's profile information
        binding.tvGreeting.text = "Hi, $displayName!"

        firebaseUser.photoUrl?.let { photoUrl ->
            // Use Glide to load the profile image
            Glide.with(this)
                .load(photoUrl)
                .into(binding.imvUserPhoto)
        }
    }

}