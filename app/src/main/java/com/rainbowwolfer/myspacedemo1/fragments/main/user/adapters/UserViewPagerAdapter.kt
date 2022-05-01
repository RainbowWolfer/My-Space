package com.rainbowwolfer.myspacedemo1.fragments.main.user.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.fragments.main.user.userprofile.UserFollowersFragment
import com.rainbowwolfer.myspacedemo1.fragments.main.user.userprofile.UserPostsFragment
import com.rainbowwolfer.myspacedemo1.fragments.main.user.userprofile.UserProfileFragment
import com.rainbowwolfer.myspacedemo1.models.User

class UserViewPagerAdapter(
	fragment: Fragment,
	private val user: User,
) : FragmentStateAdapter(fragment) {
	override fun getItemCount(): Int = 3
	
	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> UserProfileFragment(user)
			1 -> UserPostsFragment()
			2 -> UserFollowersFragment()
			else -> UserProfileFragment(user)
		}
	}
}