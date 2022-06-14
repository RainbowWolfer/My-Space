package com.rainbowwolfer.myspacedemo1.services.recyclerview.interfaces

import androidx.recyclerview.widget.DiffUtil
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil

interface IRecyclerViewAdapter<T> {
	val list: List<T>
	fun setData(new: List<T>)
	
	fun update()
}