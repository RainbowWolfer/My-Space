package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentHomeBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {
	private val binding: FragmentHomeBinding by viewBinding()
	
	private val myAdapter by lazy { MainListRecyclerViewAdapter(requireContext()) }
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.fabAdd.setOnClickListener {
			Snackbar.make(view, "You have not signed in", Snackbar.LENGTH_LONG).setAction("Sign in") {
				println("?")
			}.show()
		}
		
		binding.mainListRecyvlerView.layoutManager = LinearLayoutManager(requireContext())
		binding.mainListRecyvlerView.adapter = myAdapter
		myAdapter.setData(
			listOf(
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
				Post.generateDefault(),
			)
		)
		
		binding.mainSwipeRefreshLayout.setOnRefreshListener {
			Handler(Looper.getMainLooper()).postDelayed({
				binding.mainSwipeRefreshLayout.isRefreshing = false
			}, 2000)
		}
	}
}