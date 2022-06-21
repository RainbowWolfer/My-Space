package com.rainbowwolfer.myspacedemo1.services.application

import android.app.Application
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.PostInfo
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.addUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findAvatar
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.updateAvatar
import com.rainbowwolfer.myspacedemo1.models.api.NewCommentVote
import com.rainbowwolfer.myspacedemo1.models.api.NewPostVote
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.datastore.repositories.UserPreferencesRepository
import com.rainbowwolfer.myspacedemo1.services.room.AppDatabase
import com.rainbowwolfer.myspacedemo1.services.room.repository.AppRoomRepository
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils
import kotlinx.coroutines.*
import org.joda.time.DateTime
import retrofit2.HttpException
import java.util.*

class MySpaceApplication : Application() {
	companion object {
		lateinit var instance: MySpaceApplication
			private set
	}
	
	init {
		instance = this
		println("STARTING NOW : " + DateTime.now())
	}
	
	val applicationScope = CoroutineScope(SupervisorJob())
	
	private val room by lazy { AppDatabase.getDatabase(this) }
	val roomRepository by lazy {
		AppRoomRepository(
			room.draftsDao(),
			room.messagesDao(),
			room.messagesContactDao(),
		)
	}
	
	val userPreferencesRepository = UserPreferencesRepository(this)
	
	val currentUser: MutableLiveData<User?> by lazy { MutableLiveData(null) }
	val currentAvatar: MutableLiveData<Bitmap?> by lazy { MutableLiveData(null) }
	
	val usersPool: ArrayList<UserInfo> = arrayListOf()
	val postsPool: ArrayList<PostInfo> = arrayListOf()
	
	override fun onCreate() {
		super.onCreate()
		LocaleUtils.notifyLanguageChanged(this)
	}
	
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		val locale = LocaleUtils.getLanguagePreference(this)
		Locale.setDefault(locale)
		LocaleUtils.notifyLanguageChanged(this)
	}


//	val scoresPool: HashMap<String, Int> = hashMapOf()
	
	fun hasLoggedIn() = currentUser.value != null
	fun getCurrentID(): String = currentUser.value?.id ?: ""
	fun getCurrentEmail(): String = currentUser.value?.email ?: ""
	fun getCurrentPassword(): String = currentUser.value?.password ?: ""
	
	fun clearCurrent() {
		currentUser.value = null
		currentAvatar.value = null
	}

//	fun updateVote(postID: String, score: Int) {
//		scoresPool[postID] = score
//	}
//
//	fun findVote(postID: String): Int {
//		return scoresPool[postID] ?: 0
//	}
	
	fun updateAvatar() {
		if (currentUser.value == null) {
			return
		}
		
		CoroutineScope(Dispatchers.IO).launch {
			try {
				val response = RetrofitInstance.api.getAvatar(currentUser.value!!.id)
				withContext(Dispatchers.Main) {
					currentAvatar.value = BitmapFactory.decodeStream(response.byteStream())
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
	
	fun findOrGetAvatar(
		userID: String,
		lifecycleCoroutineScope: LifecycleCoroutineScope? = null,
		onLoadAvatar: (Bitmap) -> Unit,
	) {
		val scope = lifecycleCoroutineScope ?: CoroutineScope(Dispatchers.Main)
		scope.launch(Dispatchers.Main) {
			try {
				var avatarBitmap = usersPool.findAvatar(userID)
				if (avatarBitmap == null) {
					withContext(Dispatchers.IO) {
						try {
							val response = RetrofitInstance.api.getAvatar(userID)
							println("READING FROM THE SERVER")
							avatarBitmap = BitmapFactory.decodeStream(response.byteStream())
						} catch (ex: HttpException) {
							throw ResponseException(ex.response()!!.getHttpResponse())
						}
					}
					usersPool.updateAvatar(userID, avatarBitmap)
				}
				onLoadAvatar.invoke(avatarBitmap!!)
			} catch (ex: Exception) {
				ex.printStackTrace()
//				onException.invoke(ex)
			}
		}
	}
	
	fun findOrGetUserInfo(
		userID: String,
		onLoadUser: (User) -> Unit,
		onLoadAvatar: (Bitmap) -> Unit,
		lifecycleCoroutineScope: LifecycleCoroutineScope? = null
	) {
		val scope = lifecycleCoroutineScope ?: CoroutineScope(Dispatchers.Main)
		scope.launch(Dispatchers.Main) {
			try {
				var user = usersPool.findUser(userID)
				if (user == null) {
					user = withContext(Dispatchers.IO) {
						val response = RetrofitInstance.api.getUser(
							id = userID,
							selfID = currentUser.value?.id ?: "",
						)
						if (response.isSuccessful) {
							response.body()!!
						} else {
							throw ResponseException(response.getHttpResponse())
						}
					}
					usersPool.addUser(user)
				}
				onLoadUser.invoke(user)
				
				var avatarBitmap = usersPool.findAvatar(userID)
				if (avatarBitmap == null) {
					withContext(Dispatchers.IO) {
						try {
							val response = RetrofitInstance.api.getAvatar(user.id)
							avatarBitmap = BitmapFactory.decodeStream(response.byteStream())
						} catch (ex: HttpException) {
							throw ResponseException(ex.response()!!.getHttpResponse())
						}
					}
					usersPool.updateAvatar(user.id, avatarBitmap)
				}
				onLoadAvatar.invoke(avatarBitmap!!)
			} catch (ex: Exception) {
				ex.printStackTrace()
//				onException.invoke(ex)
			}
		}
	}
	
	
	fun votePost(postID: String, vote: Boolean?) {
		if (!hasLoggedIn()) {
			return
		}
		CoroutineScope(Dispatchers.Main).launch {
			try {
				val result = withContext(Dispatchers.IO) {
					val respose = RetrofitInstance.api.postVote(
						NewPostVote(
							postID = postID,
							userID = currentUser.value!!.id,
							cancel = vote == null,
							score = if (vote == true) 1 else 0,
							email = currentUser.value!!.email,
							password = currentUser.value!!.password,
						)
					)
					return@withContext kotlin.runCatching {
						respose.string()
					}.getOrNull()
				}
				
				println(result)
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
	
	
	fun voteComment(comment_id: String, vote: Boolean?) {
		if (!hasLoggedIn()) {
			return
		}
		CoroutineScope(Dispatchers.Main).launch {
			try {
				withContext(Dispatchers.IO) {
					val respose = RetrofitInstance.api.commentVote(
						NewCommentVote(
							commentID = comment_id,
							userID = currentUser.value!!.id,
							cancel = vote == null,
							score = if (vote == true) 1 else 0,
							email = currentUser.value!!.email,
							password = currentUser.value!!.password,
						)
					)
					return@withContext kotlin.runCatching {
						respose.string()
					}.getOrNull()
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
}