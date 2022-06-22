package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findPostInfo
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findRelativePosts
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.presetGridViewHeight
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import com.rainbowwolfer.myspacedemo1.util.SheetDialogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
	companion object {
		lateinit var instance: PostDetailFragment
		const val ARG_Post_ID = "post_id"
		
		
		fun newInstance(postID: String) = PostDetailFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_Post_ID, postID)
			}
		}
		
		
		fun updateVoteButtons(upButton: AppCompatImageButton, downButton: AppCompatImageButton, voted: Int) {
			val application = MySpaceApplication.instance
			val upIcon: Int
			val downIcon: Int
			when (voted) {
				Post.VOTE_UP -> {
					upIcon = R.drawable.ic_baseline_thumb_up_24
					downIcon = R.drawable.ic_outline_thumb_down_24
				}
				Post.VOTE_DOWN -> {
					upIcon = R.drawable.ic_outline_thumb_up_24
					downIcon = R.drawable.ic_baseline_thumb_down_24
				}
				Post.VOTE_NONE -> {
					upIcon = R.drawable.ic_outline_thumb_up_24
					downIcon = R.drawable.ic_outline_thumb_down_24
				}
				else -> {
					throw Exception("$voted not implemented")
				}
			}
			upButton.setImageDrawable(ResourcesCompat.getDrawable(application.resources, upIcon, application.theme))
			downButton.setImageDrawable(ResourcesCompat.getDrawable(application.resources, downIcon, application.theme))
		}
		
		
		fun updateRepostButton(button: AppCompatImageButton, hasReposted: Boolean) {
			val application = MySpaceApplication.instance
			button.imageTintList = ContextCompat.getColorStateList(application, getHighlightedColorID(hasReposted))
		}
		
		fun updateRepostButton(button: MaterialButton, hasReposted: Boolean) {
			val application = MySpaceApplication.instance
			button.iconTint = ContextCompat.getColorStateList(application, getHighlightedColorID(hasReposted))
		}
		
		fun updateRepostButton(image: ImageView, hasReposted: Boolean) {
			val application = MySpaceApplication.instance
			image.imageTintList = ContextCompat.getColorStateList(application, getHighlightedColorID(hasReposted))
		}
		
		
		private fun getHighlightedColorID(boolean: Boolean): Int {
			return if (boolean) R.color.purple_200 else R.color.colorControlNomal
		}
	}
	
	init {
		instance = this
	}
	
	private val binding: FragmentPostDetailBinding by viewBinding()
	private val application: MySpaceApplication = MySpaceApplication.instance
	private val viewModel: PostDetailViewModel by viewModels()
	private lateinit var postID: String
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		arguments?.let {
			postID = it.getString(ARG_Post_ID) ?: ""
			lifecycleScope.launch(Dispatchers.Main) {
				try {
					var post = application.postsPool.findPostInfo(postID)?.post
					if (post == null && application.hasLoggedIn()) {
						post = withContext(Dispatchers.IO) {
							val response = RetrofitInstance.api.getPostByID(
								postID = postID,
								email = application.currentUser.value?.email ?: "",
								password = application.currentUser.value?.password ?: "",
							)
							if (response.isSuccessful) {
								response.body()!!
							} else {
								throw ResponseException(response.getHttpResponse())
							}
						}
					}
					viewModel.post.value = post
				} catch (ex: Exception) {
					ex.printStackTrace()
					if (ex is ResponseException) {
						println(ex.response)
					}
				} finally {
					updateAvatar()
					
					val adapter = PostDetailViewPagerAdapter(this@PostDetailFragment, viewModel.post.value!!)
					binding.postDetailViewPager2.adapter = adapter
					
					binding.postDetailViewPager2.offscreenPageLimit = 3
					TabLayoutMediator(binding.postDetailTabsLayout, binding.postDetailViewPager2) { tab, position ->
						tab.text = when (position) {
							0 -> getString(R.string.comments)
							1 -> getString(R.string.reposts)
							2 -> getString(R.string.scores)
							else -> getString(R.string.undefined)
						}
					}.attach()
				}
			}
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.initializeUser()
		application.currentUser.observe(viewLifecycleOwner) {
			binding.enableUserFunctions()
		}
		
		viewModel.post.observe(viewLifecycleOwner) { post ->
			binding.loadImages(post)
			updatePost(post)
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.voted)
			updateRepostButton(binding.postDetailButtonRepost, post.hasReposted)
			binding.updateUser(post.getPublisher())
		}
		
		binding.postDetailButtonComment.buttonAction {
			binding.postDetailViewPager2.currentItem = 0//make fragment is on comment
			PostDetailCommentsFragment.instance?.focusCommentInput()
		}
		
		binding.postDetailButtonRepost.buttonAction {
			SheetDialogUtil.showRepostDialog(requireContext(), viewModel.post.value!!.id) {
				val post = viewModel.post.value!!
				post.updateReposts(1)
				application.postsPool.findRelativePosts(post.readID()).forEach {
					if (it.isRepost) {
						it.originReposts = post.reposts
					} else {
						it.reposts = post.reposts
					}
				}
				viewModel.post.value = post//refresh observer
			}
		}
		
		binding.postDetailButtonUpvote.buttonAction {
			val post = viewModel.post.value!!
			if (post.voted != Post.VOTE_UP) {
				application.votePost(post.id, true)
				if (post.voted == Post.VOTE_DOWN) {
					post.downvotes -= 1
				}
				post.voted = Post.VOTE_UP
				post.upvotes += 1
			} else {
				application.votePost(post.id, null)
				post.voted = Post.VOTE_NONE
				post.upvotes -= 1
			}
			
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.voted)
			updatePost(post)
			application.postsPool.findRelativePosts(post.readID()).forEach {
				if (it.isRepost) {
					it.originUpvotes = post.upvotes
					it.originDownvotes = post.downvotes
					it.originVoted = post.voted
				} else {
					it.upvotes = post.upvotes
					it.downvotes = post.downvotes
					it.voted = post.voted
				}
			}
		}
		
		binding.postDetailButtonDownvote.buttonAction {
			val post = viewModel.post.value!!
			if (post.voted != Post.VOTE_DOWN) {
				application.votePost(post.id, false)
				if (post.voted == Post.VOTE_UP) {
					post.upvotes -= 1
				}
				post.voted = Post.VOTE_DOWN
				post.downvotes += 1
			} else {
				application.votePost(post.id, null)
				post.voted = Post.VOTE_NONE
				post.downvotes -= 1
			}
			
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.voted)
			updatePost(post)
			application.postsPool.findRelativePosts(post.readID()).forEach {
				if (it.isRepost) {
					it.originUpvotes = post.upvotes
					it.originDownvotes = post.downvotes
					it.originVoted = post.voted
				} else {
					it.upvotes = post.upvotes
					it.downvotes = post.downvotes
					it.voted = post.voted
				}
			}
		}
	}
	
	private fun updateAvatar() {
		val id = viewModel.post.value?.publisherID ?: return
		if (application.currentUser.value?.id == id) {
			application.currentAvatar.observe(viewLifecycleOwner) {
				binding.updateAvatar(it)
			}
		} else {
			val found = application.usersPool.findUserInfo(id)
			if (found != null) {
				binding.updateAvatar(found.avatar)
			} else {
				lifecycleScope.launch(Dispatchers.Main) {
					application.findOrGetAvatar(id) {
						binding.updateAvatar(it)
					}
				}
			}
		}
	}
	
	private fun View.buttonAction(onClick: () -> Unit) {
		this.setOnClickListener {
			if (application.hasLoggedIn()) {
				lifecycleScope.launch {
					delay(50)//wait ripple animation
					onClick.invoke()
				}
			} else {
				HomeFragment.popupNotLoggedInHint()
			}
		}
	}
	
	private fun FragmentPostDetailBinding.loadImages(post: Post) {
		this.postDetailGridviewImages.presetGridViewHeight(post.imagesCount)
		val colors = ImagesDisplayGridViewAdapter.getShuffledColorLists(requireContext(), post.imagesCount)
		
		this.postDetailGridviewImages.adapter = ImagesDisplayGridViewAdapter(requireContext(), post).also {
			it.list = colors
		}
		
		ImagesDisplayGridViewAdapter.loadImages(this.postDetailGridviewImages, post, viewLifecycleOwner)
	}
	
	private fun FragmentPostDetailBinding.enableUserFunctions() {
		if (application.hasLoggedIn()) {
			this.postDetailButtonDownvote.isEnabled = true
			this.postDetailButtonRepost.isEnabled = true
			this.postDetailButtonComment.isEnabled = true
			this.postDetailButtonUpvote.isEnabled = true
		} else {
			this.postDetailButtonDownvote.isEnabled = false
			this.postDetailButtonRepost.isEnabled = false
			this.postDetailButtonComment.isEnabled = false
			this.postDetailButtonUpvote.isEnabled = false
		}
	}
	
	private fun FragmentPostDetailBinding.initializeUser() {
		this.postDetailImageAvatar.setImageBitmap(null)
		this.postDetailTextUsername.text = "..."
		this.postDetailTextEmail.text = "..."
	}
	
	private fun FragmentPostDetailBinding.updateUser(user: User?) {
		if (user == null) {
			this.postDetailTextUsername.text = requireContext().getString(R.string.not_found)
			this.postDetailTextEmail.text = requireContext().getString(R.string.not_found)
		} else {
			this.postDetailTextUsername.text = user.username
			this.postDetailTextEmail.text = user.email
		}
	}
	
	private fun FragmentPostDetailBinding.updateAvatar(bitmap: Bitmap?) {
		this.postDetailImageAvatar.setImageBitmap(bitmap)
	}
	
	fun updatePost(post: Post) {
		binding.postDetailTextContent.text = post.textContent
		binding.postDetailChipGroupTags.removeAllViews()
		post.tags.split(',').forEach {
			if (!TextUtils.isEmpty(it)) {
				binding.postDetailChipGroupTags.addView(Chip(context).apply {
					text = it
				})
			}
		}
		
		binding.postDetailTextPublishDate.text = post.publishDateTime
		binding.postDetailTextEditTimes.text = "${post.editTimes}"
		binding.postDetailTextEditDate.text = post.editDateTime
		with(if (post.editTimes == 0) View.GONE else View.VISIBLE) {
			binding.postDetailImageEdit.visibility = this
			binding.postDetailTextEditDate.visibility = this
			binding.postDetailTextEditTimes.visibility = this
		}
		
		binding.postDetailTextRepostsCount.text = "${post.reposts}"
		binding.postDetailTextCommentsCount.text = "${post.comments}"
		binding.postDetailTextScores.text = "${post.upvotes - post.downvotes}"
		
		with(if (post.isRepost) View.GONE else View.VISIBLE) {
			binding.postDetailButtonRepost.visibility = this
			binding.postDetailTextRepost.visibility = this
			binding.postDetailTextRepostsCount.visibility = this
//			(binding.postDetailViewPager2.adapter as PostDetailViewPagerAdapter).
		}
	}

//	fun setMeta(post: Post) {
//
//	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.post_detail_menu, menu)
	}
	
}