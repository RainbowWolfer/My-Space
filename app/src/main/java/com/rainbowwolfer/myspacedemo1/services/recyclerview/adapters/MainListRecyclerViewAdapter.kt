package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutEndBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.addPost
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findPostInfo
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findRelativePosts
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.DeletePost
import com.rainbowwolfer.myspacedemo1.models.api.NewCollection
import com.rainbowwolfer.myspacedemo1.models.enums.CollectionType
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.loadImages
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.presetGridViewHeight
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateRepostButton
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateVoteButtons
import com.rainbowwolfer.myspacedemo1.util.DateTimeUtils.convertToRecentFormat
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.SheetDialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainListRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val targetUserID: String = "",
) : RecyclerView.Adapter<MainListRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val ITEM_TYPE_NORMAL = 1
		const val ITEM_TYPE_END = 2
		const val ARG_POST_ID = "post_id"
	}
	
	abstract class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
	
	class RowViewHolder(val binding: MainRowLayoutBinding) : ViewHolder(binding.root)
	class EndViewHolder(val binding: MainRowLayoutEndBinding) : ViewHolder(binding.root)
	
	private var list = emptyList<Post>()
	private val application = MySpaceApplication.instance
	
	private var endTextIndex = 1
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			ITEM_TYPE_NORMAL -> RowViewHolder(MainRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			ITEM_TYPE_END -> EndViewHolder(MainRowLayoutEndBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			else -> throw Exception("$viewType is not found")
		}
	}
	
	override fun getItemViewType(position: Int): Int {
		return when (position) {
			list.size -> ITEM_TYPE_END
			else -> ITEM_TYPE_NORMAL
		}
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			application.postsPool.addPost(list[position])
			if (list[position].isRepost) {
				application.postsPool.addPost(list[position].getOriginPost()!!)
			}
			
			val postID = list[position].id
			val originID = list[position].originPostID
			val isRepost = list[position].isRepost
			
			holder.binding.rowGridviewImages.isVerticalScrollBarEnabled = false
			holder.binding.rowGridviewImages.isEnabled = false
			
			holder.binding.setPostView(postID)
			
			holder.binding.root.setOnClickListener {
				val navController = Navigation.findNavController(holder.itemView)
				navController.navigate(R.id.item_post_detail, Bundle().apply {
					putString(ARG_POST_ID, if (isRepost) originID else postID)
				}, EasyFunctions.defaultTransitionNavOption())
			}
			
			holder.binding.mainLayoutRepost.setOnClickListener {
				if (isRepost) {
					val navController = Navigation.findNavController(holder.itemView)
					navController.navigate(R.id.item_post_detail, Bundle().apply {
						putString(ARG_POST_ID, postID)
					}, EasyFunctions.defaultTransitionNavOption())
				}
			}
			
			holder.binding.rowButtonMore.setOnClickListener {
				val post = list[position]
				val popupMenu = PopupMenu(context, holder.binding.rowButtonMore)
				popupMenu.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.item_delete -> {
							AlertDialog.Builder(context).apply {
								setTitle(context.getString(R.string.confirm))
								setMessage(context.getString(R.string.are_you_sure_to_delete_post))
								setNegativeButton(context.getString(R.string.no), null)
								setPositiveButton(context.getString(R.string.yes)) { _, _ ->
									lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
										try {
											withContext(Dispatchers.IO) {
												RetrofitInstance.api.postDelete(
													DeletePost(
														postID = postID,
														email = application.getCurrentEmail(),
														password = application.getCurrentPassword(),
													)
												)
											}
											Toast.makeText(context, context.getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
										} catch (ex: Exception) {
											ex.printStackTrace()
										} finally {
											try {
												setData(list.filter { item ->
													item.id != postID
												})
											} catch (ex: Exception) {
											}
										}
									}
								}
								create()
								show()
							}
						}
						R.id.item_share -> {
							val sharedIntent = Intent().apply {
								this.action = Intent.ACTION_SEND
								this.putExtra(
									Intent.EXTRA_TEXT,
									context.getString(R.string.publisher) + " :${post.publisherUsername}\n" +
											context.getString(R.string.date) + " :${post.publishDateTime}\n" +
											post.textContent
								)
								this.type = "text/plain"
							}
							context.startActivity(sharedIntent)
						}
						R.id.item_collection -> {
							if (application.hasLoggedIn()) {
								lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
									try {
										RetrofitInstance.api.addCollection(
											NewCollection(
												targetID = postID,
												type = CollectionType.Post,
												email = application.currentUser.value?.email ?: "",
												password = application.currentUser.value?.password ?: "",
											)
										)
										withContext(Dispatchers.Main) {
											Toast.makeText(context, context.getString(R.string.successfully_added_to_collection), Toast.LENGTH_SHORT).show()
										}
									} catch (ex: Exception) {
										ex.printStackTrace()
									}
								}
							}
						}
						R.id.item_report -> Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show()
					}
					true
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					popupMenu.setForceShowIcon(true)
				}
				popupMenu.inflate(R.menu.more_button_menu)
				with(application.getCurrentID() == post.publisherID) {
					popupMenu.menu.getItem(2).also { item ->
						item.isVisible = this
						item.isEnabled = this
						if (this) {
							item.title = if (post.isRepost) {
								context.getString(R.string.delete_repost)
							} else {
								context.getString(R.string.delete)
							}
						}
					}
				}
				
				popupMenu.show()
			}
			//avatar clicking
			val post = list[position]
			if (targetUserID != post.readUser().id) {
				holder.binding.rowImagePublisherAvatar.setOnClickListener {
					if (post.isRepost) {
						navigateToUserProfile(holder.itemView, post.getOriginUser()!!.id)
					} else {
						navigateToUserProfile(holder.itemView, post.publisherID)
					}
				}
			}
			if (targetUserID != post.publisherID) {
				holder.binding.rowLayoutRepostInfo.setOnClickListener {
					if (post.isRepost) {
						navigateToUserProfile(holder.itemView, post.publisherID)
					}
				}
			}
		} else if (holder is EndViewHolder) {
			holder.binding.rowEndTextCenter.setOnClickListener {
				val array = arrayOf(
					context.getString(R.string.end_row_text_1),
					context.getString(R.string.end_row_text_2),
					context.getString(R.string.end_row_text_3),
					context.getString(R.string.end_row_text_4),
					context.getString(R.string.end_row_text_5),
				)
//				val array = context.resources.getStringArray(R.array.end_row_texts)
				holder.binding.rowEndTextCenter.text = array[endTextIndex]
				if (++endTextIndex >= array.size) {
					endTextIndex = 0
				}
			}
		}
	}
	
	private fun navigateToUserProfile(view: View, id: String) {
		val navController = Navigation.findNavController(view)
//		navController.graph.findNode(R.id.userFragment)?.label = "User $id"
//		val action = HomeFragmentDirections.actionItemHomeToUserFragment(id)
		navController.navigate(R.id.item_user_profile, Bundle().apply {
			putString("user_id", id)
		}, EasyFunctions.defaultTransitionNavOption())
	}
	
	private fun MainRowLayoutBinding.setButtons(postID: String) {
		this.rowLayoutRepost.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			SheetDialogUtils.showRepostDialog(context, post.readID()) {
				post.updateReposts(1)
				setMetas(post.readID())
				
				with(application.postsPool.findRelativePosts(post.readID())) {
					this.forEach {
						if (it.isRepost) {
							it.originReposts = post.reposts
						} else {
							it.reposts = post.reposts
						}
					}
					updatePositions(this)
				}
			}
		}
		
		this.rowLayoutComment.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			SheetDialogUtils.showCommentDialog(context, post.readID()) {
				post.updateComments(1)
				setMetas(post.readID())
				
				with(application.postsPool.findRelativePosts(post.readID())) {
					this.forEach {
						if (it.isRepost) {
							it.originComments = post.comments
						} else {
							it.comments = post.comments
						}
					}
					updatePositions(this)
				}
			}
		}
		
		this.rowButtonUpvote.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			if (post.readVoted() != Post.VOTE_UP) {
				application.votePost(post.readID(), true)
				if (post.readVoted() == Post.VOTE_DOWN) {
					post.updateDownvotes(-1)
				}
				post.updateVoted(Post.VOTE_UP)
				post.updateUpvotes(1)
			} else {
				application.votePost(post.readID(), null)
				post.updateVoted(Post.VOTE_NONE)
				post.updateUpvotes(-1)
			}
			
			updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, post.readVoted())
			setMetas(postID)
			
			with(application.postsPool.findRelativePosts(post.readID())) {
				this.forEach {
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
				updatePositions(this)
			}
		}
		
		this.rowButtonDownvote.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			if (post.readVoted() != Post.VOTE_DOWN) {
				application.votePost(post.id, false)
				if (post.readVoted() == Post.VOTE_UP) {
					post.updateUpvotes(-1)
				}
				post.updateVoted(Post.VOTE_DOWN)
				post.updateDownvotes(1)
			} else {
				application.votePost(post.id, null)
				post.updateVoted(Post.VOTE_NONE)
				post.updateDownvotes(-1)
			}
			
			updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, post.readVoted())
			setMetas(postID)
			
			with(application.postsPool.findRelativePosts(post.readID())) {
				this.forEach {
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
				updatePositions(this)
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
	
	private fun MainRowLayoutBinding.setPostView(postID: String) {
		val p = application.postsPool.findPostInfo(postID)!!.post
		this.setRepostView(p.isRepost)
		this.setContent(p)
//		val result = if (p.isRepost) p.getOriginPost()!! else p
		this.setTags(p.readTags())
		this.setMetas(p.readID())
		this.setPublisher(p.readUser())
		this.setButtons(p.readID())//include writes
		updateVoteButtons(this.rowButtonUpvote, this.rowButtonDownvote, p.readVoted())
		updateRepostButton(this.rowImageRepostButtonIcon, p.hasReposted)
		this.setImages(if (p.isRepost) p.getOriginPost()!! else p)
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
	
	private fun MainRowLayoutBinding.setContent(post: Post) {
		if (post.isRepost) {
			val origin = post.getOriginPost()!!
			this.rowTextPublishDateTime.text = origin.publishDateTime.convertToRecentFormat(context)
			//"${post.id}_${post.originPostID}" +
			this.rowTextContent.text = origin.textContent
			this.mainTextRepost.text = post.textContent
			@SuppressLint("SetTextI18n")
			this.rowTextRepostInfo.text = context.getString(R.string.repost_from) + " @${post.publisherUsername}"
			//avatar
			application.findOrGetAvatar(post.getPublisher().id) {
				this.mainImageRepostAvatar.setImageBitmap(it)
			}
		} else {
			this.rowTextPublishDateTime.text = post.publishDateTime.convertToRecentFormat(context)
			//"${post.id}_${post.originPostID}" +
			this.rowTextContent.text = post.textContent
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
	
	private fun MainRowLayoutBinding.setMetas(postID: String) {
		val post = application.postsPool.findPostInfo(postID)?.post ?: return
		this.rowRepostCount.text = "${post.readReposts()}"
		this.rowCommentCount.text = "${post.readComments()}"
		this.rowTextScore.text = "${post.readUpvotes() - post.readDownvotes()}"
	}
	
	private fun MainRowLayoutBinding.setPublisher(user: User) {
		this.rowImagePublisherAvatar.setImageDrawable(null)
		this.rowTextPublisherName.text = user.username
		application.findOrGetAvatar(user.id) {
			this.rowImagePublisherAvatar.setImageBitmap(it)
		}
	}
	
	private fun MainRowLayoutBinding.setImages(post: Post) {
		this.rowGridviewImages.presetGridViewHeight(post.imagesCount)
		if (post.imagesCount == 0) {
			return
		}
		val colors = ImagesDisplayGridViewAdapter.getShuffledColorLists(context, post.imagesCount)
		
		this.rowGridviewImages.adapter = ImagesDisplayGridViewAdapter(context, post).also {
			it.list = colors
		}
		loadImages(rowGridviewImages, post, lifecycleOwner)
	}
	
	private fun updatePositions(changes: List<Post>, onlyCompareID: Boolean = true) {
		val positions = arrayListOf<Int>()
		for (i in changes.indices) {
			for (j in list.indices) {
				if ((onlyCompareID && changes[i].id == list[j].id) || changes[i] == list[j]) {
					positions.add(j)
				}
			}
		}
		positions.forEach {
			notifyItemChanged(it)//idk but it just want to update specific elements instead of whole view
		}
	}
	
	fun setData(new: List<Post>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		list = new
		DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
		endTextIndex = 1
	}
}