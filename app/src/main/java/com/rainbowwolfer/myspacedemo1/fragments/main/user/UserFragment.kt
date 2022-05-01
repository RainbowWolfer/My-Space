package com.rainbowwolfer.myspacedemo1.fragments.main.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserBinding
import com.rainbowwolfer.myspacedemo1.fragments.main.user.adapters.UserViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.models.User

class UserFragment : Fragment(R.layout.fragment_user) {
	private val binding: FragmentUserBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val user: User? = if (arguments != null) {
			UserFragmentArgs.fromBundle(requireArguments()).user
		} else {
			User.current
		}
		
		if (user == null) {
			binding.userLayoutMain.visibility = View.GONE
			binding.userIncludeNotLoggedIn.rootNotLoggedIn.visibility = View.VISIBLE
			return
		}
		
		val adapter = UserViewPagerAdapter(this, user)
		binding.userViewPager2.adapter = adapter
		
		TabLayoutMediator(binding.userTabLayout, binding.userViewPager2) { tab, position ->
			tab.text = when (position) {
				0 -> "Profile"
				1 -> "Posts"
				2 -> "Followers"
				else -> "Undefined"
			}
		}.attach()
	}
}