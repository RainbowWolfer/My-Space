package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserFollowersBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.FollowersRecylerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment

class UserFollowersFragment(
	private val parent: UserFragment,
) : Fragment(R.layout.fragment_user_followers) {
	private val binding: FragmentUserFollowersBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adapter = FollowersRecylerViewAdapter()
		binding.followersRecylerView.layoutManager = LinearLayoutManager(requireContext())
		binding.followersRecylerView.adapter = adapter
		adapter.setData(
			listOf(
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				User.generateDefault(),
				
				)
		)
	}
}