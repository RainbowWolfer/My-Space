package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserBinding
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.adapters.UserViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivityViewModel

class UserFragment : Fragment(R.layout.fragment_user) {
	private val binding: FragmentUserBinding by viewBinding()
	private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
	private val application = MySpaceApplication.instance
	
	companion object {
		var Instance: UserFragment? = null
	}
	
	init {
		Instance = this
	}
	
	var isSelf: Boolean = false
		private set
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val user: User?
		if (arguments != null) {
			user = UserFragmentArgs.fromBundle(requireArguments()).user
			isSelf = false
		} else {
			user = application.currentUser.value
			isSelf = true
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