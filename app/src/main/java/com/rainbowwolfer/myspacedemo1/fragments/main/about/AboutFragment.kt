package com.rainbowwolfer.myspacedemo1.fragments.main.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {
	private val binding: FragmentAboutBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
	}
}