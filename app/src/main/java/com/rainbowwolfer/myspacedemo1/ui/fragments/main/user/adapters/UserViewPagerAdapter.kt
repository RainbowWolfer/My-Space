package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserFollowersFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserPostsFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserProfileFragment

class UserViewPagerAdapter(
	parent: UserFragment,
	private val userID: String,
) : FragmentStateAdapter(parent) {
	override fun getItemCount(): Int = 3
	
	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> UserProfileFragment.newInstance(userID)
			1 -> UserPostsFragment.newInstance(userID)
			2 -> UserFollowersFragment.newInstance(userID)
			else -> throw Error()
		}
	}
}