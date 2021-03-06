package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerview

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowCommentEndLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.RowCommentLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateVoteButtons

class PostCommentsRecyclerViewAdapter(
	private val fragment: Fragment,
) : RecyclerView.Adapter<PostCommentsRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val ID_ROW = 1
		const val ID_END = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowViewHolder(val binding: RowCommentLayoutBinding) : ViewHolder(binding.root)
	class EndViewHolder(val binding: RowCommentEndLayoutBinding) : ViewHolder(binding.root)
	
	private val context = fragment.requireContext()
	private var list = emptyList<Comment>()
	
	private var lastRow: EndViewHolder? = null
	
	private val application = MySpaceApplication.instance
	
	override fun getItemViewType(position: Int): Int {
		return when (position) {
			list.size -> ID_END
			else -> ID_ROW
		}
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			ID_ROW -> {
				RowViewHolder(RowCommentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			else -> {
				EndViewHolder(RowCommentEndLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			val data = list[position]
			
			holder.updateComment(data)
			holder.setMoreButton()
			updateVoteButtons(holder.binding.commentRowButtonUpvote, holder.binding.commentRowButtonDownvote, data.voted)
			
			if (application.currentUser.value?.id == data.userID) {
				application.currentUser.observe(fragment.viewLifecycleOwner) {
					holder.updateUser(it)
				}
				
				application.currentAvatar.observe(fragment.viewLifecycleOwner) {
					holder.updateAvatar(it)
				}
			} else {
				val userInfo: MutableLiveData<UserInfo> by lazy { MutableLiveData() }
				val found = application.usersPool.findUserInfo(data.userID)
				if (found != null) {
					userInfo.value = found
				} else {
					application.findOrGetUserInfo(data.userID, {
						userInfo.value = application.usersPool.findUserInfo(data.userID)
					}, {
						userInfo.value = application.usersPool.findUserInfo(data.userID)
					}, fragment.lifecycleScope)
				}
				
				userInfo.observe(fragment.viewLifecycleOwner) {
					holder.updateUser(it.user)
					holder.updateAvatar(it.avatar)
				}
			}
			
			holder.binding.commentRowButtonUpvote.buttonAction {
				if (data.voted != Comment.VOTE_UP) {
					application.voteComment(data.id, true)
					if (data.voted == Comment.VOTE_DOWN) {
						data.downvotes -= 1
					}
					data.voted = Comment.VOTE_UP
					data.upvotes += 1
				} else {
					application.voteComment(data.id, null)
					data.voted = Comment.VOTE_NONE
					data.upvotes -= 1
				}
				updateVoteButtons(holder.binding.commentRowButtonUpvote, holder.binding.commentRowButtonDownvote, data.voted)
				holder.updateComment(data)
			}
			
			holder.binding.commentRowButtonDownvote.buttonAction {
				if (data.voted != Comment.VOTE_DOWN) {
					application.voteComment(data.id, false)
					if (data.voted == Comment.VOTE_UP) {
						data.upvotes -= 1
					}
					data.voted = Comment.VOTE_DOWN
					data.downvotes += 1
				} else {
					application.voteComment(data.id, null)
					data.voted = Comment.VOTE_NONE
					data.downvotes -= 1
				}
				updateVoteButtons(holder.binding.commentRowButtonUpvote, holder.binding.commentRowButtonDownvote, data.voted)
				holder.updateComment(data)
			}
			
		} else if (holder is EndViewHolder) {
			lastRow = holder
		}
		
	}
	
	private fun View.buttonAction(onClick: () -> Unit) {
		this.setOnClickListener {
			if (application.hasLoggedIn()) {
				onClick.invoke()
			} else {
				HomeFragment.popupNotLoggedInHint(fragment.requireView())
			}
		}
	}
	
	private fun RowViewHolder.setMoreButton() {
		this.binding.commentRowButtonMore.setOnClickListener {
			val popupMenu = PopupMenu(context, this.binding.commentRowButtonMore)
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
					R.id.item_collection -> Toast.makeText(context, "Flag", Toast.LENGTH_SHORT).show()
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
	}
	
	private fun RowViewHolder.updateComment(comment: Comment) {
		this.binding.commentRowTextTime.text = comment.datetime
		this.binding.commentRowTextContent.text = comment.textContent
		this.binding.commentRowTextScore.text = "${comment.upvotes - comment.downvotes}"
	}
	
	private fun RowViewHolder.updateUser(user: User?) {
		if (user == null) {
			this.binding.commentRowTextUsername.text = context.getString(R.string.not_found)
		} else {
			this.binding.commentRowTextUsername.text = user.username
		}
	}
	
	private fun RowViewHolder.updateAvatar(bitmap: Bitmap?) {
		this.binding.commentRowImageAvatar.setImageBitmap(bitmap)
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	fun setData(new: List<Comment>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		list = new
		diffResult.dispatchUpdatesTo(this)
		
		if (lastRow != null) {
			lastRow!!.binding.endCommentRowTextInfo.text = if (list.isEmpty()) context.getString(R.string.no_comments_yet) else context.getString(R.string.no_more)
		}
	}
}