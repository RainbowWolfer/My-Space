package com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentDraftsBinding
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts.recylcerview.adapters.DraftsRecyclerViewAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DraftsFragment : Fragment(R.layout.fragment_drafts) {
	companion object {
		
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
		recyclerViewAdapter = DraftsRecyclerViewAdapter(requireContext(), lifecycleScope)
		binding.draftsRecyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
		binding.draftsRecyclerViewMain.adapter = recyclerViewAdapter
		
		viewModel.allDrafts.observe(viewLifecycleOwner) {
			binding.draftsEditSearch.text?.clear()
			setData(it)
		}
		
		binding.draftsFabAdd.setOnClickListener {
			startActivity(Intent(context, PostActivity::class.java))
		}
		
		binding.draftsEditSearch.setOnEditorActionListener { _, _, _ ->
			val text = binding.draftsEditSearch.text?.toString() ?: ""
			if (!TextUtils.isEmpty(text)) {
				val imm = requireContext().getSystemService(InputMethodManager::class.java)
				imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
				binding.draftsEditSearch.clearFocus()
				val result = viewModel.getSearched(text)
				setData(result)
				false
			} else {
				setData(viewModel.allDrafts.value ?: emptyList())
				true
			}
		}
		
		binding.draftsSwipeRefresh.setOnRefreshListener {
			lifecycleScope.launch {
				delay(500)
				binding.draftsSwipeRefresh.isRefreshing = false
				binding.draftsEditSearch.text?.clear()
				setData(viewModel.allDrafts.value ?: emptyList())
			}
		}
	}
	
	
	private fun setData(list: List<Draft>) {
		if (list.isEmpty()) {
			binding.draftsLayoutEmpty.visibility = View.VISIBLE
			binding.draftsRecyclerViewMain.visibility = View.GONE
		} else {
			binding.draftsLayoutEmpty.visibility = View.GONE
			binding.draftsRecyclerViewMain.visibility = View.VISIBLE
		}
		recyclerViewAdapter.setData(list)
	}
}