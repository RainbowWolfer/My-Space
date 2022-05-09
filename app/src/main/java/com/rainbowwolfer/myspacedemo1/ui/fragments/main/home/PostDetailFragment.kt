package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
	private var param1: String? = null
	
	private val binding: FragmentPostDetailBinding by viewBinding()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
		}
	}
	
	companion object {
		const val ARG_PARAM1 = "post"
		
		@JvmStatic
		fun newInstance(param1: String) = PostDetailFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_PARAM1, param1)
			}
		}
	}
}