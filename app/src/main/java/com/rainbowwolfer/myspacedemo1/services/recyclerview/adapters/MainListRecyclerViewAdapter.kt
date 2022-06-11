package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetCommentInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetRepostInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutEndBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragmentDirections
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.addPost
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findPostInfo
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.api.NewRepost
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.loadImages
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.presetGridViewHeight
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateRepostButton
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateVoteButtons
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainListRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val lifecycleScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<MainListRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val ITEM_TYPE_NORMAL = 1
		const val ITEM_TYPE_END = 2
		
		@JvmStatic
		fun showRepostDialog(context: Context, post: Post, successAction: (Post) -> Unit) {
			val application = MySpaceApplication.instance
			if (!application.hasLoggedIn()) {
				return
			}
			BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
				setOnShowListener {
					Handler(Looper.getMainLooper()).post {
						val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetCommentDialog_root) as? FrameLayout?
						bottomSheet?.let {
							with(BottomSheetBehavior.from(it)) {
								skipCollapsed = true
								state = BottomSheetBehavior.STATE_EXPANDED
							}
						}
					}
				}
				setCanceledOnTouchOutside(true)
				show()
				
				val binding = BottomSheetRepostInputBinding.inflate(LayoutInflater.from(context))
				setContentView(binding.root)
				
				val imm = context.getSystemService(InputMethodManager::class.java)
				imm?.showSoftInput(binding.bottomSheetRepostDialogInput, InputMethodManager.SHOW_FORCED)
				binding.bottomSheetRepostDialogInput.requestFocus()
				
				binding.bottomSheetRepostDialogButtonSend.isEnabled = false
				binding.bottomSheetRepostDialogButtonBack.setOnClickListener {
					this.dismiss()
					this.hide()
				}
				binding.bottomSheetRepostDialogEditText.doAfterTextChanged {
					binding.bottomSheetRepostDialogButtonSend.isEnabled = !it.isNullOrEmpty() && it.length <= 200
				}
				
				binding.bottomSheetRepostDialogButtonSend.setOnClickListener {
					CoroutineScope(Dispatchers.Main).launch {
						val dialog = LoadingDialog(context).apply {
							showDialog("Reposting")
						}
						try {
							withContext(Dispatchers.IO) {
								RetrofitInstance.api.repost(
									NewRepost(
										originPostID = post.id,
										publisherID = application.currentUser.value!!.id,
										textContent = binding.bottomSheetRepostDialogEditText.text?.toString() ?: "",
										postVisibility = PostVisibility.All,
										replyLimit = PostVisibility.All,
										tags = listOf("tag1", "tag2", "tag3"),
										email = application.currentUser.value!!.email,
										password = application.currentUser.value!!.password,
									)
								)
							}
							
							val found = application.postsPool.findPostInfo(post.id)?.post
							if (found != null) {
								found.reposts += 1
								post.reposts = found.reposts
								successAction(found)
							}
						} catch (ex: Exception) {
							ex.printStackTrace()
						} finally {
							dialog.hideDialog()
							this@apply.dismiss()
							this@apply.hide()
						}
					}
				}
			}
		}
		
	}
	
	abstract class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
	
	class RowViewHolder(val binding: MainRowLayoutBinding) : ViewHolder(binding.root)
	class EndViewHolder(val binding: MainRowLayoutEndBinding) : ViewHolder(binding.root)
	
	var enableAvatarClicking: Boolean = true
	
	private var postsList = emptyList<Post>()
	private val application = MySpaceApplication.instance
	
	private var endTextIndex = 0
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			ITEM_TYPE_NORMAL -> RowViewHolder(MainRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			ITEM_TYPE_END -> EndViewHolder(MainRowLayoutEndBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			else -> throw Exception("$viewType is not found")
		}
	}
	
	override fun getItemViewType(position: Int): Int {
		return when (position) {
			postsList.size -> ITEM_TYPE_END
			else -> ITEM_TYPE_NORMAL
		}
	}
	
	override fun getItemCount(): Int = postsList.size + 1
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			val post = postsList[position]
			application.postsPool.addPost(post)
			if (post.isRepost) {
				application.postsPool.addPost(post.getOriginPost()!!)
			}
			
			holder.binding.rowGridviewImages.isVerticalScrollBarEnabled = false
			holder.binding.rowGridviewImages.isEnabled = false
			
			holder.binding.setPostView(post)
			
			holder.binding.root.setOnClickListener {
				val navController = Navigation.findNavController(holder.itemView)
				navController.navigate(
					HomeFragmentDirections.actionItemHomeToPostDetailFragment(
						if (post.isRepost) post.getOriginPost()!!.id else post.id
					)
				)
			}
			
			holder.binding.mainLayoutRepost.setOnClickListener {
				if (post.isRepost) {
					val navController = Navigation.findNavController(holder.itemView)
					navController.navigate(HomeFragmentDirections.actionItemHomeToPostDetailFragment(post.id))
				}
			}
			
			holder.binding.rowButtonMore.setOnClickListener {
				val popupMenu = PopupMenu(context, holder.binding.rowButtonMore)
				popupMenu.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.item_share -> {
							val sharedIntent = Intent().apply {
								this.action = Intent.ACTION_SEND
								this.putExtra(
									Intent.EXTRA_TEXT,
									"Publisher: ${post.publisherUsername}\nDate: ${post.publishDateTime}\n${post.textContent}"
								)
								this.type = "text/plain"
							}
							context.startActivity(sharedIntent)
						}
						R.id.item_flag -> Toast.makeText(context, "Flag $post", Toast.LENGTH_SHORT).show()
						R.id.item_report -> Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show()
					}
					true
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					popupMenu.setForceShowIcon(true)
				}
				popupMenu.inflate(R.menu.more_button_menu)
				popupMenu.show()
			}
			if (enableAvatarClicking) {
				holder.binding.rowImagePublisherAvatar.setOnClickListener {
					if (post.isRepost) {
						navigateToUserProfile(holder.itemView, post.getOriginUser()!!.id)
					} else {
						navigateToUserProfile(holder.itemView, post.publisherID)
					}
				}
				holder.binding.rowLayoutRepostInfo.setOnClickListener {
					if (post.isRepost) {
						navigateToUserProfile(holder.itemView, post.publisherID)
					}
				}
			}
		} else if (holder is EndViewHolder) {
			holder.binding.rowEndTextCenter.setOnClickListener {
				val array = context.resources.getStringArray(R.array.end_row_texts)
				holder.binding.rowEndTextCenter.text = array[endTextIndex]
				if (++endTextIndex >= array.size) {
					endTextIndex = 0
				}
			}
		}
	}
	
	private fun navigateToUserProfile(view: View, id: String) {
		val navController = Navigation.findNavController(view)
		navController.graph.findNode(R.id.userFragment)?.label = "User $id"
		val action = HomeFragmentDirections.actionItemHomeToUserFragment(id)
		navController.navigate(action)
	}
	
	private fun MainRowLayoutBinding.setButtons(post: Post) {
		this.rowLayoutRepost.buttonAction {
			showRepostDialog(context, post) {
				setMetas(post)
			}
//			val found = application.postsPool.findPostInfo(post.id)?.post
//			if (found != null) {
//				found.reposts = post.reposts
//			}
		}
		
		this.rowLayoutComment.buttonAction {
			this.showCommentDialog(post)
		}
		
		this.rowButtonUpvote.buttonAction {
			if (post.isVoted() != true) {
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
			
			updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, post.isVoted())
			setMetas(post)
			val found = application.postsPool.findPostInfo(post.id)?.post
			if (found != null) {
				found.upvotes = post.upvotes
				found.downvotes = post.downvotes
			}
		}
		
		this.rowButtonDownvote.buttonAction {
			if (post.isVoted() != false) {
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
			
			updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, post.isVoted())
			setMetas(post)
			val found = application.postsPool.findPostInfo(post.id)?.post
			if (found != null) {
				found.upvotes = post.upvotes
				found.downvotes = post.downvotes
			}
		}
	}
	
	private fun MainRowLayoutBinding.showCommentDialog(post: Post) {
		BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
//			window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetCommentDialog_root) as? FrameLayout?
					bottomSheet?.let {
						with(BottomSheetBehavior.from(it)) {
							skipCollapsed = true
							state = BottomSheetBehavior.STATE_EXPANDED
						}
					}
				}
			}
			
			setCanceledOnTouchOutside(true)
			show()
			
			val commentInputBinding = BottomSheetCommentInputBinding.inflate(LayoutInflater.from(context))
			setContentView(commentInputBinding.root)
			
			commentInputBinding.bottomSheetCommentDialogInput.requestFocus()
			val imm = context.getSystemService(InputMethodManager::class.java)
			imm?.showSoftInput(commentInputBinding.bottomSheetCommentDialogInput, InputMethodManager.SHOW_IMPLICIT)
			
			commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = false
			commentInputBinding.bottomSheetCommentDialogButtonBack.setOnClickListener {
				this.dismiss()
				this.hide()
			}
			commentInputBinding.bottomSheetCommentDialogEditText.doAfterTextChanged {
				commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = !it.isNullOrEmpty() && it.length <= 200
			}
			commentInputBinding.bottomSheetCommentDialogButtonSend.setOnClickListener {
				lifecycleScope.launch(Dispatchers.Main) {
					val dialog = LoadingDialog(context).apply {
						showDialog("Uploading Comment")
					}
					try {
						this@showCommentDialog.rowLayoutComment.isEnabled = false
						val comment: Comment = withContext(Dispatchers.IO) {
							val response = RetrofitInstance.api.postComment(
								NewComment(
									application.currentUser.value?.email ?: "",
									application.currentUser.value?.password ?: "",
									post.id,
									commentInputBinding.bottomSheetCommentDialogEditText.text?.toString() ?: ""
								)
							)
							if (response.isSuccessful) {
								return@withContext response.body()!!
							} else {
								throw Exception()
							}
						}
						
						val found = application.postsPool.findPostInfo(post.id)?.post
						if (found != null) {
							found.comments += 1
							post.comments = found.comments
							setMetas(found)
						}
					} catch (ex: Exception) {
						ex.printStackTrace()
					} finally {
						this@showCommentDialog.rowLayoutComment.isEnabled = true
						dialog.hideDialog()
						this@apply.dismiss()
						this@apply.hide()
					}
				}
			}
		}
	}
	
	private fun View.buttonAction(onClick: () -> Unit) {
		this.setOnClickListener {
			if (application.hasLoggedIn()) {
				onClick.invoke()
			} else {
				HomeFragment.popupNotLoggedInHint()
			}
		}
	}
	
	private fun MainRowLayoutBinding.setPostView(post: Post) {
		this.setRepostView(post.isRepost)
		this.setContent(post)
		val result = if (post.isRepost) post.getOriginPost()!! else post
		this.setTags(result.tags)
		this.setMetas(result)
		this.setPublisher(result)
		this.setButtons(result)
		this.loadImages(result)
		updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, result.isVoted())
		updateRepostButton(this.rowImageRepostButtonIcon, post.hasReposted)
	}
	
	private fun MainRowLayoutBinding.setRepostView(isRepost: Boolean) {
		arrayListOf(
			this.mainLayoutRepost,
			this.mainTextRepost,
			this.mainImageRepostAvatar,
			this.rowTextRepostInfo,
			this.rowImageRepostIcon,
		).forEach {
			(if (isRepost) View.VISIBLE else View.GONE).apply {
				it.visibility = this
			}
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun MainRowLayoutBinding.setContent(post: Post) {
		if (post.isRepost) {
			val origin = post.getOriginPost()!!
			this.rowTextPublishDateTime.text = origin.publishDateTime
			this.rowTextContent.text = "${post.id}_${post.originPostID}" + origin.textContent
			this.mainTextRepost.text = post.textContent
			this.rowTextRepostInfo.text = "Repost From @${post.publisherUsername}"
			//avatar
			application.findOrGetAvatar(post.getPublisher().id) {
				this.mainImageRepostAvatar.setImageBitmap(it)
			}
		} else {
			this.rowTextPublishDateTime.text = post.publishDateTime
			this.rowTextContent.text = "${post.id}_${post.originPostID}" + post.textContent
		}
	}
	
	private fun MainRowLayoutBinding.setTags(tags: String) {
		this.rowChipGroupsTags.removeAllViews()
		tags.split(',').forEach {
			if (!TextUtils.isEmpty(it)) {
				this.rowChipGroupsTags.addView(Chip(context).apply {
					text = it
				})
			}
		}
	}
	
	private fun MainRowLayoutBinding.setMetas(post: Post) {
		this.rowRepostCount.text = "${post.reposts}"
		this.rowCommentCount.text = "${post.comments}"
//		val score = post.score + when (post.voted) {
//			Post.VOTE_NONE -> 0
//			Post.VOTE_DOWN -> -1
//			Post.VOTE_UP -> +1
//			else -> 0
//		}
		this.rowTextScore.text = "${post.upvotes - post.downvotes}"
	}
	
	private fun MainRowLayoutBinding.setPublisher(post: Post) {
		this.rowImagePublisherAvatar.setImageDrawable(null)
		this.rowTextPublisherName.text = post.getPublisher().username
		application.findOrGetAvatar(post.publisherID) {
			this.rowImagePublisherAvatar.setImageBitmap(it)
		}
	}
	
	private fun MainRowLayoutBinding.loadImages(post: Post) {
		this.rowGridviewImages.presetGridViewHeight(post.imagesCount)
		if (post.imagesCount == 0) {
			return
		}
		val colors = ImagesDisplayGridViewAdapter.getShuffledColorLists(context, post.imagesCount)
		
		this.rowGridviewImages.adapter = ImagesDisplayGridViewAdapter(context, post).also {
			it.list = colors
		}
		loadImages(this.rowGridviewImages, post, lifecycleOwner, lifecycleScope)
	}
	
	fun setData(newPersonList: List<Post>) {
		val diffUtil = DatabaseIdDiffUtil(postsList, newPersonList)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		postsList = newPersonList
		diffResult.dispatchUpdatesTo(this)
		endTextIndex = 0
	}
}