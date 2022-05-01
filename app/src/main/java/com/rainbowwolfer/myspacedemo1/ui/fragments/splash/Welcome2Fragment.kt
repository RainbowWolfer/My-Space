package com.rainbowwolfer.myspacedemo1.ui.fragments.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.viewpager2.widget.ViewPager2
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentWelcome2Binding


class Welcome2Fragment : Fragment(R.layout.fragment_welcome2) {
	private val binding: FragmentWelcome2Binding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val viewPager = activity?.findViewById<ViewPager2>(R.id.welcome_viewPager)
		binding.welcom2ButtonPrevious.setOnClickListener {
			viewPager?.currentItem = 0
		}
		binding.welcome2ButtonFinish.setOnClickListener {
			SplashFragment.jumpToMainActivity(this)
		}
	}
}