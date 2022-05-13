package com.rainbowwolfer.myspacedemo1.models.application

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.PostInfo
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo

class MySpaceApplication : Application() {
	companion object {
		lateinit var instance: MySpaceApplication
			private set
		
		const val IMAGES_COUNT_PER_POST = 9
	}
	
	init {
		instance = this
	}
	
	val currentUser: MutableLiveData<User?> by lazy { MutableLiveData(null) }
	val currentAvatar: MutableLiveData<Bitmap?> by lazy { MutableLiveData(null) }
	
	val usersPool: ArrayList<UserInfo> = arrayListOf()
	val postImagesPool: ArrayList<PostInfo> = arrayListOf()
	
	fun hasLoggedIn() = currentUser.value != null
	
	fun clearCurrent() {
		currentUser.value = null
		currentAvatar.value = null
	}
	
}