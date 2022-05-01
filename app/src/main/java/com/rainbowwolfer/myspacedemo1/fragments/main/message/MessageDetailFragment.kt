package com.rainbowwolfer.myspacedemo1.fragments.main.message

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageDetailBinding
import com.rainbowwolfer.myspacedemo1.models.MessageItem
import com.rainbowwolfer.myspacedemo1.models.MessageSet
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageDetailRecyclerViewAdapter


class MessageDetailFragment : Fragment(R.layout.fragment_message_detail) {
	private val binding: FragmentMessageDetailBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.messageDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
			reverseLayout = true
		}
		
		val adapter = MessageDetailRecyclerViewAdapter(requireContext())
		binding.messageDetailRecyclerView.adapter = adapter
		
		adapter.setData(MessageSet.generateDefault())
		/**
		 *
		 * listOf(
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		MessageItem.generateDefault(),
		)
		 */
	}
}