package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserPostsBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter

class UserPostsFragment : Fragment(R.layout.fragment_user_posts) {
	private val binding: FragmentUserPostsBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val adapter = MainListRecyclerViewAdapter(requireContext()).also {
			it.enableAvatarClicking = false
		}
		
		binding.userRecyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
		binding.userRecyclerViewPosts.adapter = adapter
		adapter.setData(
			listOf(
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
			)
		)
	}
}