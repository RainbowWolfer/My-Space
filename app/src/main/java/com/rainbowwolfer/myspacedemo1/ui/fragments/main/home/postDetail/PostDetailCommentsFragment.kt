package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailCommentsBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.PostCommentsRecylverViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostDetailCommentsFragment : Fragment(R.layout.fragment_post_detail_comments) {
	companion object {
		lateinit var Instance: PostDetailCommentsFragment
		private const val ARG_POST_ID = "post_id"
		
		@JvmStatic
		fun newInstance(postID: String) = PostDetailCommentsFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_POST_ID, postID)
			}
		}
	}
	
	init {
		Instance = this
	}
	
	private lateinit var postID: String
	
	private val binding: FragmentPostDetailCommentsBinding by viewBinding()
	private val viewModel: PostDetailViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			postID = it.getString(ARG_POST_ID, "")
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adapter = PostCommentsRecylverViewAdapter(requireContext(), viewLifecycleOwner)
		binding.postDetailCommentsRecylverView.isNestedScrollingEnabled = true
		binding.postDetailCommentsRecylverView.layoutManager = LinearLayoutManager(requireContext())
		binding.postDetailCommentsRecylverView.adapter = adapter
		
		
		loadComment(postID)
		
		viewModel.comments.observe(viewLifecycleOwner) {
			adapter.setData(it)
			binding.postDetailCommentsRecylverView.smoothScrollToPosition(0)
		}
		
		hideLoading()
		
		binding.postDetailCommentsEditTextComment.setOnEditorActionListener { _, _, _ ->
			val text = binding.postDetailCommentsEditTextComment.text?.toString() ?: ""
			if (TextUtils.isEmpty(text) || text.length > 200) {
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
				binding.postDetailCommentsInputComment.hint = "Leave a nice comment"
				binding.postDetailCommentsInputComment.isEnabled = true
			} else {
				binding.postDetailCommentsInputComment.hint = "You have not logged in"
				binding.postDetailCommentsInputComment.isEnabled = false
			}
		}
	}
	
	private fun showLoading(title: String = "") {
		binding.postDetailTextLoading.text = title
		binding.root.transitionToStart()
	}
	
	private fun hideLoading() {
		binding.root.transitionToEnd()
	}
	
	
	private fun loadComment(postID: String) {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				showLoading("Loading Comments")
				val comments: List<Comment>
				withContext(Dispatchers.IO) {
					val response = RetrofitInstance.api.getPostComments(postID)
					if (response.isSuccessful) {
						comments = response.body() ?: emptyList()
					} else {
						throw ResponseException(response.getHttpResponse())
					}
				}
				viewModel.comments.value = comments
			} catch (ex: Exception) {
				ex.printStackTrace()
			} finally {
				hideLoading()
			}
		}
	}
	
	private fun uploadComment(postID: String, content: String) {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				binding.postDetailCommentsInputComment.isEnabled = false
				showLoading("Uploading Comment")
				withContext(Dispatchers.IO) {
					RetrofitInstance.api.postComment(
						NewComment(
							application.currentUser.value?.email ?: "",
							application.currentUser.value?.password ?: "",
							postID,
							content
						)
					)
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			} finally {
				hideLoading()
				binding.postDetailCommentsInputComment.isEnabled = true
			}
		}
	}
	
	fun focusCommentInput() {
		binding.postDetailCommentsEditTextComment.requestFocus()
		val imm = requireActivity().getSystemService(InputMethodManager::class.java)
		imm?.showSoftInput(binding.postDetailCommentsEditTextComment, InputMethodManager.SHOW_IMPLICIT)
	}
	
}