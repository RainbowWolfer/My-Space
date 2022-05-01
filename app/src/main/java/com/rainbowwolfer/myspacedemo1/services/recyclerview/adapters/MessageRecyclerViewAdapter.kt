package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.MessageRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.MessageSet
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil

class MessageRecyclerViewAdapter(

) : RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: MessageRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	private var messages = emptyList<MessageSet>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(MessageRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = messages[position]
		holder.binding.messageRowCardViewRoot.setOnClickListener {
			Navigation.findNavController(holder.itemView).navigate(R.id.action_nav_message_to_nav_detailMessage)
		}
	}
	
	override fun getItemCount(): Int = messages.size
	
	fun setData(new: List<MessageSet>) {
		val diffUtil = DatabaseIdDiffUtil(messages, new)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		messages = new
		diffResult.dispatchUpdatesTo(this)
	}
}