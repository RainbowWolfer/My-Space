package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowMessageEmptyLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.RowMessageLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.MessageDetailFragment
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.convertToRecentFormat

class MessageRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val TYPE_ROW = 0
		const val TYPE_EMPTY = 1
		const val TYPE_IGNORE = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowViewHolder(val binding: RowMessageLayoutBinding) : ViewHolder(binding.root)
	class EmptyViewHolder(val binding: RowMessageEmptyLayoutBinding) : ViewHolder(binding.root)
	
	private var list = emptyList<MessageContact>()
	private val application = MySpaceApplication.instance
	
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
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPE_EMPTY -> {
				EmptyViewHolder(RowMessageEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			TYPE_ROW -> {
				RowViewHolder(RowMessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			else -> {
				ViewHolder(View(context))
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			val data = list[position]
			
			holder.binding.messageRowTextUsername.text = data.username
			holder.binding.messageRowTextDatetime.text = data.dateTime.convertToRecentFormat(context)
			holder.binding.messageRowTextContent.text = data.textContent
			
			application.findOrGetAvatar(data.senderID.toString()) {
				holder.binding.messageRowImageAvatar.setImageBitmap(it)
			}
			
			holder.binding.messageRowCardViewRoot.setOnClickListener {
				Navigation.findNavController(holder.itemView).navigate(R.id.action_nav_message_to_nav_detailMessage, Bundle().apply {
					putParcelable(MessageDetailFragment.ARG_CONTACT, data)
				})
			}
		}
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	fun setData(new: List<MessageContact>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		list = new
		DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
	}
}