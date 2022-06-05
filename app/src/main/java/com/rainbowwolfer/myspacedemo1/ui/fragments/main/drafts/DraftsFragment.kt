package com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentDraftsBinding
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts.recylcerview.adapters.DraftsRecyclerViewAdapter

class DraftsFragment : Fragment(R.layout.fragment_drafts) {
	companion object {
		@JvmStatic
		fun newInstance() = DraftsFragment().apply {
			arguments = Bundle().apply {
				
			}
		}
	}
	
	private val binding: FragmentDraftsBinding by viewBinding()
	private val viewModel: DraftsFragmentViewModel by viewModels()
	
	private lateinit var recyclerViewAdapter: DraftsRecyclerViewAdapter
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		recyclerViewAdapter = DraftsRecyclerViewAdapter(requireContext())
		binding.draftsRecyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
		binding.draftsRecyclerViewMain.adapter = recyclerViewAdapter
		
		viewModel.allDrafts.observe(viewLifecycleOwner) {
			if (it.isEmpty()) {
				binding.draftsLayoutEmpty.visibility = View.VISIBLE
				binding.draftsRecyclerViewMain.visibility = View.GONE
			} else {
				binding.draftsLayoutEmpty.visibility = View.GONE
				binding.draftsRecyclerViewMain.visibility = View.VISIBLE
			}
			recyclerViewAdapter.setData(it)
		}
		
		binding.draftsFabAdd.setOnClickListener {
			startActivity(Intent(context, PostActivity::class.java))
		}
		
	}
}