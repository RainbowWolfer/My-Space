package com.rainbowwolfer.myspacedemo1.ui.fragments.main.login.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.ui.activities.login.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.login.LoginFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.login.SignUpFragment

class LoginViewPagerAdapter(
	private val loginActivity: LoginActivity,
	fragmentManager: FragmentManager,
	lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
	override fun getItemCount(): Int = 2
	
	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> LoginFragment()
			1 -> SignUpFragment()
			else -> LoginFragment()
		}
	}
}