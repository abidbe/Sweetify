package com.abidbe.sweetify.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abidbe.sweetify.R
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
        binding.tvGreeting.text = getString(R.string.greeting_text, displayName)

        firebaseUser.photoUrl?.let { photoUrl ->
            // Use Glide to load the profile image
            Glide.with(this)
                .load(photoUrl)
                .into(binding.imvUserPhoto)
        }

        // Navigate to GradeDetailFragment when a card is clicked
        binding.cardA.setOnClickListener {
            navigateToGradeDetail("A")
        }
        binding.cardB.setOnClickListener {
            navigateToGradeDetail("B")
        }
        binding.cardC.setOnClickListener {
            navigateToGradeDetail("C")
        }
        binding.cardD.setOnClickListener {
            navigateToGradeDetail("D")
        }

        // Navigate to SearchFragment when search bar is clicked
        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        binding.viewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_glupediaFragment)
        }
        binding.imvUserPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.tvGreeting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun navigateToGradeDetail(grade: String) {
        val bundle = Bundle().apply {
            putString("GRADE", grade)
        }
        findNavController().navigate(R.id.action_homeFragment_to_gradeDetailFragment, bundle)
    }
}
