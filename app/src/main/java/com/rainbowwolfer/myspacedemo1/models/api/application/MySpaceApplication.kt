package com.rainbowwolfer.myspacedemo1.models.api.application

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.User

class MySpaceApplication : Application() {
	companion object {
		lateinit var instance: MySpaceApplication
			private set
	}
	
	init {
		instance = this
	}
	
	val currentUser: MutableLiveData<User?> by lazy { MutableLiveData(null) }
	val currentAvatar: MutableLiveData<Bitmap?> by lazy { MutableLiveData(null) }
	
	fun hasLoggedIn() = currentUser.value != null
	
	fun clearCurrent() {
		currentUser.value = null
		currentAvatar.value = null
	}
	
}