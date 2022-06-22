package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailScoresBinding
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerview.PostScoresRecordRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostDetailScoresFragment : Fragment(R.layout.fragment_post_detail_scores) {
	companion object {
		const val ARG_POST_ID = "post_id"
		const val RELOAD_THRESHOLD = 3
		
		
		fun newInstance(postID: String) = PostDetailScoresFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_POST_ID, postID)
			}
		}
	}
	
	private lateinit var postID: String
	private val binding: FragmentPostDetailScoresBinding by viewBinding()
	private val viewModel: PostDetailViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val adapter by lazy { PostScoresRecordRecyclerViewAdapter(requireContext(), viewLifecycleOwner, lifecycleScope) }
	private val application = MySpaceApplication.instance
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			postID = it.getString(ARG_POST_ID) ?: ""
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.postDetailScoresRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.postDetailScoresRecyclerView.adapter = adapter
		
		loadScoresRecord(true)
		
		viewModel.scoreRecords.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		
		binding.postDetailScoresRecyclerView.scrollToUpdate {
			loadScoresRecord(false)
		}
	}
	
	private fun enableLoading(boolean: Boolean) {
		binding.postDetailScoresRecyclerView.visibility = if (boolean) View.GONE else View.VISIBLE
		binding.postDetailScoresLoadingBar.visibility = if (!boolean) View.GONE else View.VISIBLE
	}
	
	private fun loadScoresRecord(refresh: Boolean) {
		if (isLoading) {
			return
		}
		isLoading = true
		if (refresh) {
			enableLoading(true)
		}
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				EasyFunctions.stackLoading(refresh, viewModel.scoreRecords, viewModel.scoreRecordsOffset) {
					RetrofitInstance.api.getScoreRecords(postID, viewModel.scoreRecordsOffset.value ?: 0)
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			} finally {
				try {
					isLoading = false
					enableLoading(false)
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
	}
	
}