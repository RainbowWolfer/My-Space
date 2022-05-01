package com.rainbowwolfer.myspacedemo1.ui.fragments.main.message

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageBinding
import com.rainbowwolfer.myspacedemo1.models.MessageSet
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageRecyclerViewAdapter

class MessageFragment : Fragment(R.layout.fragment_message) {
	private val binding: FragmentMessageBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.messageSwipeRefreshLayout.setOnRefreshListener {
			Handler(Looper.getMainLooper()).postDelayed({
				binding.messageSwipeRefreshLayout.isRefreshing = false
			}, 2000)
		}
		
		binding.messageFabAdd.setOnClickListener {
			Snackbar.make(view, "You have not signed in", Snackbar.LENGTH_LONG).setAction("Sign in") {
			
			}.show()
		}
		
		binding.messageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		val messageRecyclerViewAdapter = MessageRecyclerViewAdapter()
		binding.messageRecyclerView.adapter = messageRecyclerViewAdapter
		messageRecyclerViewAdapter.setData(
			listOf(
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
				MessageSet.generateDefault(),
			)
		)
	}
}