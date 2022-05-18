package com.rainbowwolfer.myspacedemo1.models.application

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.PostInfo
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.addUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findAvatar
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.updateAvatar
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
	
	
	suspend fun findOrGet(userID: String, onLoadUser: (User) -> Unit, onLoadAvatar: (Bitmap) -> Unit, onException: (Exception) -> Unit = { }) {
		try {
			var user = usersPool.findUser(userID)
			if (user == null) {
				withContext(Dispatchers.IO) {
					val response = RetrofitInstance.api.getUser(userID)
					if (response.isSuccessful) {
						user = response.body()!!
					} else {
						val go = response.getHttpResponse()
						throw ResponseException(go)
					}
				}
				usersPool.addUser(user!!)
			}
			onLoadUser.invoke(user!!)
			
			var avatarBitmap = usersPool.findAvatar(userID)
			if (avatarBitmap == null) {
				withContext(Dispatchers.IO) {
					try {
						val response = RetrofitInstance.api.getAvatar(user!!.id)
						avatarBitmap = BitmapFactory.decodeStream(response.byteStream())
					} catch (ex: HttpException) {
						throw ResponseException(ex.response()!!.getHttpResponse())
					}
				}
				usersPool.updateAvatar(user!!.id, avatarBitmap)
			}
			onLoadAvatar.invoke(avatarBitmap!!)
		} catch (ex: Exception) {
			ex.printStackTrace()
			onException.invoke(ex)
		}
	}
}