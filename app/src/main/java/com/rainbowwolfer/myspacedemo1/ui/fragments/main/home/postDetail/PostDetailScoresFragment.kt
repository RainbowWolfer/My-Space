package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailScoresBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.models.records.ScoreRecord
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerviews.PostScoresRecordRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostDetailScoresFragment : Fragment(R.layout.fragment_post_detail_scores) {
	companion object {
		const val ARG_POST_ID = "post_id"
		const val RELOAD_THRESHOLD = 3
		
		@JvmStatic
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
		println("enable: $boolean")
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
				var triedCount = 0
				var list: List<ScoreRecord> = if (refresh) emptyList() else viewModel.scoreRecords.value!!
				do {
					val newList: List<ScoreRecord> = withContext(Dispatchers.IO) {
						val response = RetrofitInstance.api.getScoreRecords(postID, viewModel.scoreRecordsOffset.value ?: 0)
						if (response.isSuccessful) {
							response.body() ?: emptyList()
						} else {
							throw ResponseException(response.getHttpResponse())
						}
					}
					
					var count = 0
					if (newList.isNotEmpty()) {
						for (item in newList) {
							if (list.any { it.likeID == item.likeID }) {
								continue
							}
							list = list.plus(item)
							count++
						}
						viewModel.scoreRecordsOffset.value = viewModel.scoreRecordsOffset.value!!.plus(count)
					}
					
					viewModel.scoreRecords.value = list
				} while (newList.isNotEmpty() && count <= RELOAD_THRESHOLD && triedCount++ <= 5)
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