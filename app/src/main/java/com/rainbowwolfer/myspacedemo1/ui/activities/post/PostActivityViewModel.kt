package com.rainbowwolfer.myspacedemo1.ui.activities.post

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility

//import com.rainbowwolfer.myspacedemo1.util.VarPair


class PostActivityViewModel : ViewModel() {
	val content: MutableLiveData<String> by lazy { MutableLiveData("") }
	val images: MutableLiveData<Array<Triple<Bitmap, ByteArray, String>?>> by lazy { MutableLiveData(arrayOfNulls(9)) }
	
	val postVisibility: MutableLiveData<PostVisibility> by lazy { MutableLiveData(PostVisibility.All) }
	val replyVisiblity: MutableLiveData<PostVisibility> by lazy { MutableLiveData(PostVisibility.All) }
	
	val tags: MutableLiveData<ArrayList<String>> by lazy { MutableLiveData(arrayListOf()) }
	
	fun hasTag(tag: String): Boolean {
		return tags.value?.any {
			it == tag
		} ?: false
	}
	
	fun addTag(tag: String) {
		val t = tag.trim()
		if (hasTag(t)) {
			return
		}
		val clone = tags.value!!
		clone.add(t)
		tags.value = clone
	}
	
	fun removeTag(tag: String) {
		val t = tag.trim()
		if (!hasTag(t) || tags.value == null) {
			return
		}
		val clone = tags.value!!
		clone.remove(t)
		tags.value = clone
	}
	
	fun getImages() = images.value!!
	fun getContent() = content.value!!
	
	fun getBitmaps(): List<Bitmap?> {
		return images.value!!.map {
			it?.first
		}
	}
	
	fun getByteArrays(): List<ByteArray?> {
		return images.value!!.map {
			it?.second
		}
	}
	
	fun getExtensions(): List<String?> {
		return images.value!!.map {
			it?.third
		}
	}
	
	private fun setImages(array: Array<Triple<Bitmap, ByteArray, String>?>) {
		images.value = array
	}
	
	fun clearImages() {
		for (i in images.value!!.indices) {
			images.value!![i] = null
		}
	}

//	fun sortImages() {
//		val clone = getImages()
//		clone.sortWith(compareBy(nullsFirst()) {
//			it == null
//		})
//		setImages(clone)
//	}
	
	private fun Array<Triple<Bitmap, ByteArray, String>?>.sortImages() {
		this.sortWith(compareBy(nullsFirst()) {
			it == null
		})
	}
	
	fun addImage(image: Triple<Bitmap, ByteArray, String>) {
		val index = getImages().indexOfFirst {
			it == null
		}
		if (index == -1) {
			System.err.println("Index cannot be -1")
			return
		}
		val clone = getImages()
		clone[index] = image
		clone.sortImages()
		setImages(clone)
	}
	
	fun removeImage(index: Int) {
		if (index !in getImages().indices) {
			System.err.println("Index is not within array range")
			return
		}
		val clone = getImages()
		clone[index] = null
		clone.sortImages()
		setImages(clone)
	}
}