package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.viewbinding.library.fragment.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailBinding
import com.rainbowwolfer.myspacedemo1.models.Post

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
	private var post: Post? = null
	
	private val binding: FragmentPostDetailBinding by viewBinding()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			post = it.getParcelable(ARG_Post)
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		if (post == null) {
			post = PostDetailFragmentArgs.fromBundle(requireArguments()).post
		}
		
		val adapter = PostDetailViewPagerAdapter(this, post!!)
		binding.postDetailViewPager2.adapter = adapter
		
		binding.postDetailViewPager2.offscreenPageLimit = 3
		TabLayoutMediator(binding.postDetailTabsLayout, binding.postDetailViewPager2) { tab, position ->
			tab.text = when (position) {
				0 -> "Comments"
				1 -> "Reposts"
				2 -> "Scores"
				else -> "Undefined"
			}
		}.attach()
	}
	
	companion object {
		const val ARG_Post = "post"
		
		@JvmStatic
		fun newInstance(post: Post) = PostDetailFragment().apply {
			arguments = Bundle().apply {
				putParcelable(ARG_Post, post)
			}
		}
	}
}