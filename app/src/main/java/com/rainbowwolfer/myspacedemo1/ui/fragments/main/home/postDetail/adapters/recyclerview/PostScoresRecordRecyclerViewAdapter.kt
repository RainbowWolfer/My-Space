package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowEmptyLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.RowScoreRecordLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.records.ScoreRecord
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil

class PostScoresRecordRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<PostScoresRecordRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val TYPE_ROW = 0
		const val TYPE_EMPTY = 1
		const val TYPE_IGNORE = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowViewHolder(val binding: RowScoreRecordLayoutBinding) : ViewHolder(binding.root)
	class EmptyViewHolder(val binding: RowEmptyLayoutBinding) : ViewHolder(binding.root)
	
	private var list: List<ScoreRecord> = emptyList()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPE_EMPTY -> {
				EmptyViewHolder(RowEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			TYPE_ROW -> {
				RowViewHolder(RowScoreRecordLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			else -> {
				ViewHolder(View(context))
			}
		}
	}
	
	override fun getItemViewType(position: Int): Int {
		return if (list.isEmpty()) {
			TYPE_EMPTY
		} else {
			if (position >= list.size) {
				TYPE_IGNORE
			} else {
				TYPE_ROW
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			val data = list[position]
			holder.binding.rowScoreRecordTextUsername.text = data.username
			holder.binding.rowScoreRecordTextTime.text = data.time
			
			with(holder.binding.rowScoreRecordButtonVote) {
				when (data.voted) {
					Post.VOTE_UP -> {
						this.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_outline_thumb_up_24))
						this.imageTintList = AppCompatResources.getColorStateList(context, R.color.green)
					}
					Post.VOTE_DOWN -> {
						this.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_outline_thumb_down_24))
						this.imageTintList = AppCompatResources.getColorStateList(context, R.color.red)
					}
				}
			}
			
			val application = MySpaceApplication.instance
			
			if (application.currentUser.value?.id == data.userID) {
				application.currentAvatar.observe(lifecycleOwner) {
					holder.binding.rowScoreRecordImageAvatar.setImageBitmap(it)
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
					}, lifecycleCoroutineScope)
				}
				
				userInfo.observe(lifecycleOwner) {
					holder.binding.rowScoreRecordImageAvatar.setImageBitmap(it.avatar)
				}
			}
		}
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	fun setData(new: List<ScoreRecord>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		list = new
		DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
	}
}