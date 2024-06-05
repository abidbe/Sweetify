package com.abidbe.sweetify.view.glupedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abidbe.sweetify.databinding.FragmentGlupediaBinding


class GlupediaFragment : Fragment() {
    private lateinit var binding: FragmentGlupediaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlupediaBinding.inflate(inflater, container, false)
        return binding.root
    }

}