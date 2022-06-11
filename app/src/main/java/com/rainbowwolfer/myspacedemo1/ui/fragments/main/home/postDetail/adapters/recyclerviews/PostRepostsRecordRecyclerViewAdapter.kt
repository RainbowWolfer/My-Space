package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.adapters.recyclerviews

import android.content.Context
import android.text.TextUtils
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
import com.rainbowwolfer.myspacedemo1.databinding.RowRepostRecordLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.records.RepostRecord
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment

class PostRepostsRecordRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<PostRepostsRecordRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val TYPE_ROW = 0
		const val TYPE_EMPTY = 1
		const val TYPE_IGNORE = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowViewHolder(val binding: RowRepostRecordLayoutBinding) : ViewHolder(binding.root)
	class EmptyViewHolder(val binding: RowEmptyLayoutBinding) : ViewHolder(binding.root)
	
	private var list: List<RepostRecord> = emptyList()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPE_EMPTY -> {
				EmptyViewHolder(RowEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			TYPE_ROW -> {
				RowViewHolder(RowRepostRecordLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
			holder.binding.rowRepostRecordTextUsername.text = data.username
			holder.binding.rowRepostRecordTextTime.text = data.time
			holder.binding.rowRepostRecordTextQuote.text = data.quote
			
			if (data.userID == MySpaceApplication.instance.currentUser.value?.id) {
				PostDetailFragment.updateRepostButton(holder.binding.rowRepostRecordIconRepost, true)
			}
			
			if (!TextUtils.isEmpty(data.quote)) {
				holder.binding.rowRepostRecordButtonQuote.visibility = View.VISIBLE
				holder.binding.root.setOnClickListener {
					if (holder.binding.root.tag == true) {
						holder.binding.rowRepostRecordButtonQuote.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_format_quote_24))
						holder.binding.root.tag = false
						holder.binding.rowRepostRecordTextQuote.visibility = View.GONE
					} else {
						holder.binding.rowRepostRecordButtonQuote.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_outline_format_quote_24))
						holder.binding.root.tag = true
						holder.binding.rowRepostRecordTextQuote.visibility = View.VISIBLE
					}
				}
			} else {
				holder.binding.rowRepostRecordButtonQuote.visibility = View.GONE
			}
			holder.binding.rowRepostRecordImageAvatar.setOnClickListener {
			
			}
			
			val application = MySpaceApplication.instance
			
			if (application.currentUser.value?.id == data.userID) {
				application.currentAvatar.observe(lifecycleOwner) {
					holder.binding.rowRepostRecordImageAvatar.setImageBitmap(it)
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
					holder.binding.rowRepostRecordImageAvatar.setImageBitmap(it.avatar)
				}
			}
		}
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	fun setData(new: List<RepostRecord>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		list = new
		diffResult.dispatchUpdatesTo(this)
	}
}