package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserFollowersFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserPostsFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile.UserProfileFragment
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment

class UserViewPagerAdapter(
	private val parent: UserFragment,
	private val user: User,
) : FragmentStateAdapter(parent) {
	override fun getItemCount(): Int = 3
	
	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> UserProfileFragment.newInstance(user)
			1 -> UserPostsFragment()
			2 -> UserFollowersFragment()
			else -> UserProfileFragment.newInstance(user)
		}
	}
}