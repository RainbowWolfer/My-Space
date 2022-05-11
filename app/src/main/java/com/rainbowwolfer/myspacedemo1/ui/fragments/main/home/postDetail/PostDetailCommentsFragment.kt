package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailCommentsBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.PostCommentsRecylverViewAdapter

class PostDetailCommentsFragment : Fragment(R.layout.fragment_post_detail_comments) {
	private var comments: ArrayList<Comment> = arrayListOf()
	
	private val binding: FragmentPostDetailCommentsBinding by viewBinding()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			comments = it.getParcelableArrayList(ARG_COMMENTS) ?: arrayListOf()
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adapter = PostCommentsRecylverViewAdapter()
		binding.postDetailCommentsRecylverView.isNestedScrollingEnabled = true
		binding.postDetailCommentsRecylverView.layoutManager = LinearLayoutManager(requireContext())
		binding.postDetailCommentsRecylverView.adapter = adapter
		adapter.setData(comments)
	}
	
	companion object {
		private const val ARG_COMMENTS = "comments"
		
		@JvmStatic
		fun newInstance(comments: ArrayList<Comment>) =
			PostDetailCommentsFragment().apply {
				arguments = Bundle().apply {
					putParcelableArrayList(ARG_COMMENTS, comments)
				}
			}
	}
}