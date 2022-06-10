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
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentPostDetailBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.presetGridViewHeight
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MainListRecyclerViewAdapter.Companion.showRepostDialog
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
	companion object {
		lateinit var instance: PostDetailFragment
		const val ARG_Post = "post"
		
		@JvmStatic
		fun newInstance(post: Post) = PostDetailFragment().apply {
			arguments = Bundle().apply {
				putParcelable(ARG_Post, post)
			}
		}
		
		@JvmStatic
		fun updateVoteButtons(upButton: AppCompatImageButton, downButton: AppCompatImageButton, vote: Boolean?) {
			val application = MySpaceApplication.instance
			val upIcon: Int
			val downIcon: Int
			when (vote) {
				true -> {
					upIcon = R.drawable.ic_baseline_thumb_up_24
					downIcon = R.drawable.ic_outline_thumb_down_24
				}
				false -> {
					upIcon = R.drawable.ic_outline_thumb_up_24
					downIcon = R.drawable.ic_baseline_thumb_down_24
				}
				null -> {
					upIcon = R.drawable.ic_outline_thumb_up_24
					downIcon = R.drawable.ic_outline_thumb_down_24
				}
			}
			upButton.setImageDrawable(ResourcesCompat.getDrawable(application.resources, upIcon, application.theme))
			downButton.setImageDrawable(ResourcesCompat.getDrawable(application.resources, downIcon, application.theme))
		}
		
		@JvmStatic
		fun updateRepostButton(button: AppCompatImageButton, hasReposted: Boolean) {
			val application = MySpaceApplication.instance
			button.imageTintList = ContextCompat.getColorStateList(application, getHighlightedColorID(hasReposted))
		}
		
		@JvmStatic
		fun updateRepostButton(image: ImageView, hasReposted: Boolean) {
			val application = MySpaceApplication.instance
			image.imageTintList = ContextCompat.getColorStateList(application, getHighlightedColorID(hasReposted))
		}
		
		@JvmStatic
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
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		arguments?.let {
			viewModel.post.value = it.getParcelable(ARG_Post)
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
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.isVoted())
			updateRepostButton(binding.postDetailButtonRepost, post.hasReposted)
			binding.updateUser(post.getPublisher())
		}
		
		val id = viewModel.post.value!!.publisherID
		if (application.currentUser.value?.id == id) {
			application.currentAvatar.observe(viewLifecycleOwner) {
				binding.updateAvatar(it)
			}
		} else {
			val found = application.usersPool.findUserInfo(id)
			if (found != null) {
				binding.updateAvatar(found.avatar)
			} else {
				CoroutineScope(Dispatchers.Main).launch {
					application.findOrGetAvatar(id) {
						binding.updateAvatar(it)
					}
				}
			}
		}
		
		
		val adapter = PostDetailViewPagerAdapter(this, viewModel.post.value!!)
		binding.postDetailViewPager2.adapter = adapter
		
		binding.postDetailViewPager2.offscreenPageLimit = 3
		TabLayoutMediator(binding.postDetailTabsLayout, binding.postDetailViewPager2) { tab, position ->
			tab.text = when (position) {
				0 -> "Comments"
				1 -> "Reposts"
				2 -> "Scores"
				else -> "Undefined"
			}
		}.attach()
		
		binding.postDetailButtonComment.buttonAction {
			PostDetailCommentsFragment.instance?.focusCommentInput()
		}
		
		binding.postDetailButtonRepost.buttonAction {
			showRepostDialog(requireContext(), viewModel.post.value!!) {
				viewModel.post.value = it
			}
		}
		
		binding.postDetailButtonUpvote.buttonAction {
			val post = viewModel.post.value!!
			if (post.isVoted() != true) {
				application.vote(post.id, true)
				if (post.voted == Post.VOTE_DOWN) {
					post.downvotes -= 1
				}
				post.voted = Post.VOTE_UP
				post.upvotes += 1
			} else {
				application.vote(post.id, null)
				post.voted = Post.VOTE_NONE
				post.upvotes -= 1
			}
			
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.isVoted())
			updatePost(post)
		}
		
		binding.postDetailButtonDownvote.buttonAction {
			val post = viewModel.post.value!!
			if (post.isVoted() != false) {
				application.vote(post.id, false)
				if (post.voted == Post.VOTE_UP) {
					post.upvotes -= 1
				}
				post.voted = Post.VOTE_DOWN
				post.downvotes += 1
			} else {
				application.vote(post.id, null)
				post.voted = Post.VOTE_NONE
				post.downvotes -= 1
			}
			
			updateVoteButtons(binding.postDetailButtonUpvote, binding.postDetailButtonDownvote, post.isVoted())
			updatePost(post)
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
		
		ImagesDisplayGridViewAdapter.loadImages(this.postDetailGridviewImages, post, viewLifecycleOwner, lifecycleScope)
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
			this.postDetailTextUsername.text = "Not found"
			this.postDetailTextEmail.text = "Not found"
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
		
		binding.postDetailButtonRepost.visibility = if (post.isRepost) View.GONE else View.VISIBLE
	}

//	fun setMeta(post: Post) {
//
//	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.post_detail_menu, menu)
	}
	
}