package com.rainbowwolfer.myspacedemo1.fragments.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {
	private val binding: FragmentSplashBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		Handler(Looper.getMainLooper()).postDelayed({
			loadNext()
		}, 2000)
	}
	
	private fun loadNext() {
		val pref = requireActivity().getSharedPreferences("Splash", Context.MODE_PRIVATE)
		if (pref.getBoolean("Finished", false)) {
			jumpToMainActivity(this)
		} else {
//			findNavController().navigate(R.id.action_splashFragment_to_welcomeViewPagerFragment)
		}
	}
	
	companion object {
		@JvmStatic
		fun jumpToMainActivity(fragment: Fragment) {
			fragment.startActivity(Intent(fragment.requireContext(), MainActivity::class.java))
			fragment.activity?.finish()
			val edit = fragment.requireActivity().getSharedPreferences("Splash", Context.MODE_PRIVATE).edit()
			edit.putBoolean("Finished", true)
			edit.apply()
		}
	}
}