package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.databinding.RowCommentLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil

class PostCommentsRecylverViewAdapter(

) : RecyclerView.Adapter<PostCommentsRecylverViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: RowCommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	private var list = emptyList<Comment>()
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RowCommentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = list[position]
		
	}
	
	override fun getItemCount(): Int = list.size
	
	fun setData(new: List<Comment>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		list = new
		diffResult.dispatchUpdatesTo(this)
	}
}