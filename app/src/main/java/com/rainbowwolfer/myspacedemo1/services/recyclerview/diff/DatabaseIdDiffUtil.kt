package com.rainbowwolfer.myspacedemo1.services.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID

class DatabaseIdDiffUtil<T, W : DatabaseID<T>>(
	private val oldList: List<W>,
	private val newList: List<W>,
) : DiffUtil.Callback() {
	override fun getOldListSize(): Int = oldList.size
	
	override fun getNewListSize(): Int = newList.size
	
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition].getDatabaseID() == newList[newItemPosition].getDatabaseID()
	}
	
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition] == newList[newItemPosition]
	}
}