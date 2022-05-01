package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.databinding.UserFollowRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil

class FollowersRecylerViewAdapter(

) : RecyclerView.Adapter<FollowersRecylerViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: UserFollowRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	private var usersList = emptyList<User>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(UserFollowRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = usersList[position]
		holder.binding.followersTextUsername.text = data.username
	}
	
	override fun getItemCount(): Int = usersList.size
	
	fun setData(newPersonList: List<User>) {
		val diffUtil = DatabaseIdDiffUtil(usersList, newPersonList)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		usersList = newPersonList
		diffResult.dispatchUpdatesTo(this)
	}
}