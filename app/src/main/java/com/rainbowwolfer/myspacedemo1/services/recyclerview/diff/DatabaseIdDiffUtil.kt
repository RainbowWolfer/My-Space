package com.rainbowwolfer.myspacedemo1.services.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID

class DatabaseIdDiffUtil<T : DatabaseID>(
	private val oldList: List<T>,
	private val newList: List<T>,
) : DiffUtil.Callback() {
	override fun getOldListSize(): Int = oldList.size
	
	override fun getNewListSize(): Int = newList.size
	
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].id == newList[newItemPosition].id
	}
	
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition] == newList[newItemPosition]
	}
}