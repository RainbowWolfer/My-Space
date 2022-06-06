package com.rainbowwolfer.myspacedemo1.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class BitmapLoader(
	private val postID: String,
	private val index: Int,
) {
	init {
		if (index !in 0..8) {
			throw Exception("index can only be in 0 between 8")
		}
	}
	
	private var isLoading = false
	val bitmap: MutableLiveData<Bitmap?> by lazy { MutableLiveData(null) }
	
	private var response: ResponseBody? = null
	suspend fun load() {
		if (isLoading && response == null) {
			return
		}
		isLoading = true
		var decodeBitmap: Bitmap
		withContext(Dispatchers.IO) {
			response = RetrofitInstance.api.getPostImage(postID, index)
			decodeBitmap = BitmapFactory.decodeStream(response!!.byteStream())
			isLoading = false
			response = null
		}
		withContext(Dispatchers.Main) {
			bitmap.value = decodeBitmap
		}
	}
	
	fun hasValue(): Boolean = bitmap.value != null && !isLoading
}