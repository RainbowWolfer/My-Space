package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail

import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels.PostDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {
	companion object {
		lateinit var Instance: PostDetailFragment
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
		
	}
	
	init {
		Instance = this
	}
	
	private var _post: Post? = null
	private val post: Post get() = _post!!
	
	private val binding: FragmentPostDetailBinding by viewBinding()
	private val application: MySpaceApplication = MySpaceApplication.instance
	private val viewModel: PostDetailViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		arguments?.let {
			_post = it.getParcelable(ARG_Post)
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.initializeUser()
		application.currentUser.observe(viewLifecycleOwner) {
			binding.enableUserFunctions()
		}
		binding.loadImages()
		
		if (_post == null) {
			_post = PostDetailFragmentArgs.fromBundle(requireArguments()).post
		}
		
		updatePost(post)
		binding.updateUser(post.getPublisher())
		val id = post.publisherID
		
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
		
		
		val adapter = PostDetailViewPagerAdapter(this, post)
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
		
		binding.postDetailButtonComment.setOnClickListener {
			PostDetailCommentsFragment.Instance.focusCommentInput()
		}
	}
	
	private fun FragmentPostDetailBinding.loadImages() {
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
		binding.postDetailTextScores.text = "${post.score}"
		
		binding.postDetailButtonRepost.visibility = if (post.isRepost) View.GONE else View.VISIBLE
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.post_detail_menu, menu)
	}
	
}