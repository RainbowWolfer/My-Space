package com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay.adapters

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay.ZoomImageFragment

class ImagesPoolViewPagerAdapter(
	fragmentManager: FragmentManager,
	lifecycle: Lifecycle,
	private val postID: String,
	private val size: Int,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
	override fun getItemCount(): Int = size
	
	override fun createFragment(position: Int): Fragment {
		return ZoomImageFragment.newInstance(postID, position)
	}
	
	
}