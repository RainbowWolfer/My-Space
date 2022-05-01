package com.rainbowwolfer.myspacedemo1.fragments.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentWelcomeViewPagerBinding
import com.rainbowwolfer.myspacedemo1.fragments.splash.adapters.WelcomeViewPagerAdapter

class WelcomeViewPagerFragment : Fragment(R.layout.fragment_welcome_view_pager) {
	private val binding: FragmentWelcomeViewPagerBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val fragments = ArrayList(
			listOf(
				Welcome1Fragment(),
				Welcome2Fragment(),
			)
		)
		val adapter = WelcomeViewPagerAdapter(
			fragments,
			requireActivity().supportFragmentManager,
			lifecycle
		)
		binding.welcomeViewPager.adapter = adapter
	}
}