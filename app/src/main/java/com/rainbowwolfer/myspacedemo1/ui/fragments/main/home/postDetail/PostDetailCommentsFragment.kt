package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailCommentsBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findPostInfo
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findRelativePosts
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.FragmentCustomBackPressed
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerview.PostCommentsRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostDetailCommentsFragment : Fragment(R.layout.fragment_post_detail_comments), FragmentCustomBackPressed {
	companion object {
		var instance: PostDetailCommentsFragment? = null
		private const val ARG_POST_ID = "post_id"
		
		fun newInstance(postID: String) = PostDetailCommentsFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_POST_ID, postID)
			}
		}
	}
	
	init {
		instance = this
	}
	
	private lateinit var postID: String
	
	private val binding: FragmentPostDetailCommentsBinding by viewBinding()
	private val viewModel: PostDetailViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	
	private val adapter by lazy { PostCommentsRecyclerViewAdapter(this) }
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			postID = it.getString(ARG_POST_ID, "")
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.postDetailCommentsRecyclerView.isNestedScrollingEnabled = true
		binding.postDetailCommentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.postDetailCommentsRecyclerView.adapter = adapter
		
		loadComment(true, postID)
		
		viewModel.comments.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		
		hideLoading()
		
		binding.postDetailCommentsEditTextComment.setOnEditorActionListener { _, _, _ ->
			val text = binding.postDetailCommentsEditTextComment.text?.toString() ?: ""
			if (text.isBlank() || text.length > 200) {
				return@setOnEditorActionListener true
			} else {
				uploadComment(postID, text)
				binding.postDetailCommentsEditTextComment.text?.clear()
				binding.postDetailCommentsEditTextComment.clearFocus()
				return@setOnEditorActionListener false //false closes keyboard
			}
		}
		
		binding.postDetailCommentsEditTextComment.setOnFocusChangeListener { _, hasFocus ->
			binding.postDetailCommentsInputComment.isCounterEnabled = hasFocus
		}
		
		application.currentUser.observe(viewLifecycleOwner) {
			if (application.hasLoggedIn()) {
				binding.postDetailCommentsInputComment.hint = getString(R.string.leave_a_nice_comment)
				binding.postDetailCommentsInputComment.isEnabled = true
			} else {
				binding.postDetailCommentsInputComment.hint = getString(R.string.you_have_not_signed_in)
				binding.postDetailCommentsInputComment.isEnabled = false
			}
		}
		
		binding.postDetailCommentsRecyclerView.scrollToUpdate {
			loadComment(false, postID)
		}
	}
	
	private fun showLoading(title: String = "") {
		binding.postDetailTextLoading.text = title
		binding.root.transitionToStart()
	}
	
	private fun hideLoading() {
		binding.root.transitionToEnd()
	}
	
	
	private fun loadComment(refresh: Boolean, postID: String) {
		if (isLoading) {
			return
		}
		isLoading = true
		try {
			lifecycleScope.launch(Dispatchers.Main) {
				try {
					if (refresh) {
						showLoading(getString(R.string.loading_comments))
					}
					
					EasyFunctions.stackLoading(refresh, viewModel.comments, viewModel.commentsOffset) {
						RetrofitInstance.api.getPostComments(
							postID = postID,
							offset = viewModel.commentsOffset.value ?: 0,
							email = application.currentUser.value?.email ?: "",
							password = application.currentUser.value?.password ?: ""
						)
					}
				} catch (ex: Exception) {
					ex.printStackTrace()
				} finally {
					try {
						hideLoading()
						isLoading = false
						if (refresh) {
							binding.postDetailCommentsRecyclerView.smoothScrollToPosition(0)
						}
					} catch (ex: Exception) {
						//it's ok
					}
				}
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}
	
	private fun uploadComment(postID: String, content: String) {
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				binding.postDetailCommentsInputComment.isEnabled = false
				showLoading(getString(R.string.uploading_comment))
				val comment: Comment
				withContext(Dispatchers.IO) {
					val response = RetrofitInstance.api.postComment(
						NewComment(
							application.currentUser.value?.email ?: "",
							application.currentUser.value?.password ?: "",
							postID,
							content
						)
					)
					if (response.isSuccessful) {
						comment = response.body()!!
					} else {
						throw Exception()
					}
				}
				
				val post = application.postsPool.findPostInfo(postID)?.post
				if (post != null) {
					post.updateComments(1)
					PostDetailFragment.instance.updatePost(post)
					application.postsPool.findRelativePosts(post.readID()).forEach {
						if (it.isRepost) {
							it.originComments = post.comments
						} else {
							it.comments = post.comments
						}
					}
				}
				
				viewModel.comments.value = (viewModel.comments.value ?: emptyList()).toMutableList().apply {
					add(0, comment)
				}
				viewModel.post.value = post
				
			} catch (ex: Exception) {
				ex.printStackTrace()
			} finally {
				binding.postDetailCommentsInputComment.isEnabled = true
				hideLoading()
			}
		}
	}
	
	fun focusCommentInput() {
		binding.postDetailCommentsEditTextComment.requestFocus()
		val imm = requireActivity().getSystemService(InputMethodManager::class.java)
		imm?.showSoftInput(binding.postDetailCommentsEditTextComment, InputMethodManager.SHOW_IMPLICIT)
	}
	
	override fun onBackPressed(): Boolean {
		return if (binding.postDetailCommentsEditTextComment.hasFocus()) {
			val imm = requireActivity().getSystemService(InputMethodManager::class.java)
			imm?.showSoftInput(binding.postDetailCommentsEditTextComment, InputMethodManager.HIDE_NOT_ALWAYS)
			binding.postDetailCommentsEditTextComment.clearFocus()
			true
		} else {
			false
		}
	}
	
	
}