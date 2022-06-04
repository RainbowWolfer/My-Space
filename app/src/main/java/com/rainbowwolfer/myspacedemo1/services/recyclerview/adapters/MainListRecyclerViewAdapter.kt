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
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetCommentInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutEndBinding
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragmentDirections
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.addPost
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.loadImages
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter.Companion.presetGridViewHeight
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainListRecyclerViewAdapter(
	private val context: Context,
) : RecyclerView.Adapter<MainListRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val ITEM_TYPE_NORMAL = 1
		const val ITEM_TYPE_END = 2
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
			application.postImagesPool.addPost(post)
			
			holder.binding.rowGridviewImages.isVerticalScrollBarEnabled = false
			holder.binding.rowGridviewImages.isEnabled = false
			
			holder.binding.setPostView(post)
			
			holder.binding.root.setOnClickListener {
				val navController = Navigation.findNavController(holder.itemView)
				navController.navigate(
					HomeFragmentDirections.actionItemHomeToPostDetailFragment(
						if (post.isRepost) post.getOriginPost()!! else post
					)
				)
			}
			
			holder.binding.mainLayoutRepost.setOnClickListener {
				if (post.isRepost) {
					val navController = Navigation.findNavController(holder.itemView)
					navController.navigate(HomeFragmentDirections.actionItemHomeToPostDetailFragment(post))
				}
			}
			
			holder.binding.rowButtonMore.setOnClickListener {
				val popupMenu = PopupMenu(context, holder.binding.rowButtonMore)
				popupMenu.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.item_share -> {
							val sharedIntent = Intent().apply {
								this.action = Intent.ACTION_SEND
								this.putExtra(Intent.EXTRA_TEXT, "This is a test")
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
		
		}
		
		this.rowLayoutComment.buttonAction {
			this.showCommentDialog(post)
		}
		
		this.rowButtonDownvote.buttonAction {
		
		}
		
		this.rowButtonUpvote.buttonAction {
			
		}
	}
	
	private fun MainRowLayoutBinding.showCommentDialog(post: Post) {
		BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetCommentDialog_root) as? FrameLayout?
					bottomSheet?.let {
						BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
					}
				}
			}
			
			setCanceledOnTouchOutside(true)
			show()
			
			val commentInputBinding = BottomSheetCommentInputBinding.inflate(LayoutInflater.from(context))
			setContentView(commentInputBinding.root)
			
			commentInputBinding.bottomSheetCommentDialogInput.requestFocus()
			
			commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = false
			commentInputBinding.bottomSheetCommentDialogButtonBack.setOnClickListener {
				this.dismiss()
				this.hide()
			}
			commentInputBinding.bottomSheetCommentDialogEditText.doAfterTextChanged {
				commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = !it.isNullOrEmpty() && it.length <= 200
			}
			commentInputBinding.bottomSheetCommentDialogButtonSend.setOnClickListener {
				CoroutineScope(Dispatchers.Main).launch {
					val dialog = LoadingDialog(context).apply {
						showDialog("Uploading Comment")
					}
					try {
						this@showCommentDialog.rowLayoutComment.isEnabled = false
						withContext(Dispatchers.IO) {
							RetrofitInstance.api.postComment(
								NewComment(
									application.currentUser.value?.email ?: "",
									application.currentUser.value?.password ?: "",
									post.id,
									commentInputBinding.bottomSheetCommentDialogEditText.text?.toString() ?: ""
								)
							)
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
		setRepostView(post.isRepost)
		this.setContent(post)
		val result = if (post.isRepost) post.getOriginPost()!! else post
		this.setTags(result.tags)
		this.setMetas(result.reposts, result.comments, result.downvotes, result.upvotes)
		this.setPublisher(result)
		this.setButtons(result)
		this.loadImages(result)
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
			this.rowTextContent.text = origin.textContent
			this.mainTextRepost.text = post.textContent
			this.rowTextRepostInfo.text = "Repost From @${post.publisherUsername}"
			//avatar
			application.findOrGetAvatar(post.getPublisher().id) {
				this.mainImageRepostAvatar.setImageBitmap(it)
			}
		} else {
			this.rowTextPublishDateTime.text = post.publishDateTime
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
	
	private fun MainRowLayoutBinding.setMetas(repost: Int, comment: Int, down: Int, up: Int) {
		this.rowRepostCount.text = "$repost"
		this.rowCommentCount.text = "$comment"
		this.rowTextScore.text = "${up - down}"
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
		val colors = ImagesDisplayGridViewAdapter.getShuffledColorLists(context, post.imagesCount)
		
		this.rowGridviewImages.adapter = ImagesDisplayGridViewAdapter(context, post).also {
			it.list = colors
		}
		loadImages(this.rowGridviewImages, post)
	}
	
	fun setData(newPersonList: List<Post>) {
		val diffUtil = DatabaseIdDiffUtil(postsList, newPersonList)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		postsList = newPersonList
		diffResult.dispatchUpdatesTo(this)
		endTextIndex = 0
	}
}