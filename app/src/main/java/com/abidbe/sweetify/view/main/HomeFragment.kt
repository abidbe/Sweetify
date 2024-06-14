package com.abidbe.sweetify.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.FragmentHomeBinding
import com.abidbe.sweetify.view.welcome.OnboardingActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
            activity?.finish()
            return
        }

        setupUser(firebaseUser)
    }

    private fun setupUser(firebaseUser: FirebaseUser) {
        val displayName = firebaseUser.displayName
        binding.tvGreeting.text = getString(R.string.greeting_text, displayName)
        Log.d("HomeFragment", "Photo URL: ${firebaseUser.photoUrl}")
        firebaseUser.photoUrl?.let { photoUrl ->
            Glide.with(requireContext())
                .load(photoUrl)
                .into(binding.imvUserPhoto)
        }
    }
}