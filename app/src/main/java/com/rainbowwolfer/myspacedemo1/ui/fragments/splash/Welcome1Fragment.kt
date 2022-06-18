package com.rainbowwolfer.myspacedemo1.ui.fragments.splash

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentWelcome1Binding

class Welcome1Fragment : Fragment(R.layout.fragment_welcome1) {
	private val binding: FragmentWelcome1Binding by viewBinding()
	
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val viewPager = activity?.findViewById<ViewPager2>(R.id.welcome_viewPager)
		binding.welcome1ButtonNext.setOnClickListener {
			viewPager?.currentItem = 1
		}
	}
}