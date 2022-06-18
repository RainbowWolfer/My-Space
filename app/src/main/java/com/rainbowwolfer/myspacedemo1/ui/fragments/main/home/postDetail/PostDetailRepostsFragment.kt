package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailRepostsBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerview.PostRepostsRecordRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostDetailRepostsFragment : Fragment(R.layout.fragment_post_detail_reposts) {
	companion object {
		const val ARG_POST_ID = "post_id"
		const val RELOAD_THRESHOLD = 3
		
		
		fun newInstance(postID: String) = PostDetailRepostsFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_POST_ID, postID)
			}
		}
	}
	
	private lateinit var postID: String
	private val binding: FragmentPostDetailRepostsBinding by viewBinding()
	private val viewModel: PostDetailViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	private val adapter by lazy { PostRepostsRecordRecyclerViewAdapter(requireContext(), viewLifecycleOwner, lifecycleScope) }
	
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			postID = it.getString(ARG_POST_ID) ?: ""
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		println("Start Here $postID")
		binding.postDetailRepostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.postDetailRepostsRecyclerView.adapter = adapter
		
		loadRepostRecords(true)
		
		viewModel.repostRecords.observe(viewLifecycleOwner) {
			println("SET ${it.size}")
			adapter.setData(it)
		}
		
		binding.postDetailRepostsRecyclerView.scrollToUpdate {
			loadRepostRecords(false)
		}
	}
	
	private fun enableLoading(boolean: Boolean) {
		binding.postDetailRepostsRecyclerView.visibility = if (boolean) View.GONE else View.VISIBLE
		binding.postDetailRepostsLoadingBar.visibility = if (!boolean) View.GONE else View.VISIBLE
	}
	
	private fun loadRepostRecords(refresh: Boolean) {
		println("LOADING: refresh:$refresh loading:$isLoading")
		if (isLoading) {
			return
		}
		isLoading = true
		if (refresh) {
			enableLoading(true)
		}
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				EasyFunctions.stackLoading(refresh, viewModel.repostRecords, viewModel.repostRecordsOffset) {
					RetrofitInstance.api.getRepostRecords(postID, viewModel.repostRecordsOffset.value ?: 0)
				}
//				var triedCount = 0
//				var list: List<RepostRecord> = if (refresh) emptyList() else viewModel.repostRecords.value!!
//				do {
//					val new: List<RepostRecord> = withContext(Dispatchers.IO) {
//						val response = RetrofitInstance.api.getRepostRecords(postID, viewModel.repostRecordsOffset.value ?: 0)
//						if (response.isSuccessful) {
//							response.body() ?: emptyList()
//						} else {
//							throw ResponseException(response.getHttpResponse())
//						}
//					}
//
//					var count = 0
//					if (new.isNotEmpty()) {
//						for (item in new) {
//							if (list.any { it.postID == item.postID }) {
//								continue
//							}
//							list = list.plus(item)
//							count++
//						}
//						viewModel.repostRecordsOffset.value = viewModel.repostRecordsOffset.value!!.plus(count)
//					}
//
//					viewModel.repostRecords.value = list
//				} while (new.isNotEmpty() && count <= RELOAD_THRESHOLD && triedCount++ <= 5)
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is ResponseException) {
					println(ex.response)
				}
			} finally {
				kotlin.runCatching {
					isLoading = false
					enableLoading(false)
				}
			}
		}
	}
}