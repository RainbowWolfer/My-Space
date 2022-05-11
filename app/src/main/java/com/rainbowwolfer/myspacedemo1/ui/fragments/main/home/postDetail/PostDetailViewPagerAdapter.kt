package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.Post

class PostDetailViewPagerAdapter(
	private val parent: PostDetailFragment,
	private val post: Post,
) : FragmentStateAdapter(parent) {
	override fun getItemCount(): Int = 3
	
	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> PostDetailCommentsFragment.newInstance(Comment.getTestList())
			1 -> PostDetailRepostsFragment()
			2 -> PostDetailScoresFragment()
			else -> throw Exception()
		}
	}
}