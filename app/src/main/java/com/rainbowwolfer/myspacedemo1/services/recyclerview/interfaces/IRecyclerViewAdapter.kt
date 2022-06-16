package com.rainbowwolfer.myspacedemo1.services.recyclerview.interfaces

interface IRecyclerViewAdapter<T> {
	val list: List<T>
	fun setData(new: List<T>)
}