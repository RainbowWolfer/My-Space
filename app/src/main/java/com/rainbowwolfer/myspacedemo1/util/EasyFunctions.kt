package com.rainbowwolfer.myspacedemo1.util

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.api.GoResponse
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.callbacks.ArgsCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

object EasyFunctions {
	@JvmStatic
	private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
	
	@JvmStatic
	fun generateRandomString(length: Int = 10): String {
		return (1..length)
			.map {
				Random.nextInt(0, charPool.size)
			}
			.map(charPool::get)
			.joinToString("")
	}
	
	@JvmStatic
	fun <T> generateRandomList(
		minCount: Int, maxCount: Int, argsCallBack: ArgsCallBack<T>,
	): ArrayList<T> {
		val list = arrayListOf<T>()
		val min = if (minCount > maxCount) maxCount else minCount
		val count = Random.nextInt(min, maxCount)
		for (i in 0..count) {
			list.add(argsCallBack.getArgs())
		}
		return list
	}
	
	@JvmStatic
	fun formatDateTime(calendar: Calendar): String {
		val year = calendar.get(Calendar.YEAR).toDuoNumber()
		val month = (calendar.get(Calendar.MONTH) + 1).toDuoNumber()
		val day = calendar.get(Calendar.DAY_OF_MONTH).toDuoNumber()
		
		val hour = calendar.get(Calendar.HOUR_OF_DAY).toDuoNumber()
		val minute = calendar.get(Calendar.MINUTE).toDuoNumber()
		val second = calendar.get(Calendar.SECOND).toDuoNumber()
		
		return "$year-$month-$day $hour:$minute:$second"
	}
	
	@JvmStatic
	fun Calendar.toFormatDateTime(): String {
		return formatDateTime(this)
	}
	
	fun Int.toDuoNumber(): String {
		return if (this < 10) {
			"0$this"
		} else {
			"$this"
		}
	}
	
	@JvmStatic
	fun convertMD5(input: String): String {
		val md = MessageDigest.getInstance("MD5")
		return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
	}
	
	@JvmStatic
	fun String.toMD5(): String {
		return convertMD5(this)
	}
	
	@JvmStatic
	fun Response<*>.getHttpResponse(): GoResponse {
		return try {
			val jsonResponse = JSONObject(this.errorBody()?.string().toString()).toString()
			Gson().fromJson(jsonResponse, GoResponse::class.java)
		} catch (ex: Exception) {
			ex.printStackTrace()
			GoResponse(0, "Http Response Json Convert Error", 0)
		}
	}
	
	@JvmStatic
	fun getBytes(inputStream: InputStream): ByteArray {
		val byteBuffer = ByteArrayOutputStream()
		val bufferSize = 1024
		val buffer = ByteArray(bufferSize)
		var len: Int
		while (inputStream.read(buffer).also { len = it } != -1) {
			byteBuffer.write(buffer, 0, len)
		}
		return byteBuffer.toByteArray()
	}
	
	@JvmStatic
	fun AppCompatActivity.setAutoClearEditTextFocus(event: MotionEvent) {
		if (event.action == MotionEvent.ACTION_DOWN) {
			val v = currentFocus
			if (v is EditText || v is TextInputLayout) {
				val outRect = Rect()
				v.getGlobalVisibleRect(outRect)
				if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
					v.clearFocus()
					val imm = getSystemService(InputMethodManager::class.java)
					imm.hideSoftInputFromWindow(v.windowToken, 0)
				}
			}
		}
	}
	
	
	@JvmStatic
	fun RecyclerView.scrollToUpdate(threashold: Int = 2, updateAction: () -> Unit) {
		if (this.adapter == null) {
			return
		}
		this.addOnScrollListener(
			object : RecyclerView.OnScrollListener() {
				override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
					super.onScrolled(recyclerView, dx, dy)
					
					val lastPosition = (this@scrollToUpdate.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
//						val firstPosition = (this@scrollToUpdate.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
					
					if (lastPosition >= adapter!!.itemCount - threashold) {
						updateAction.invoke()
					}
				}
			}
		)
	}
	
	
	private const val RELOAD_THRESHOLD = 3
	private const val MAX_TRY_COUNT = 5
	
	@JvmStatic
	suspend fun <T : DatabaseID<W>, W> stackLoading(refresh: Boolean, data: MutableLiveData<List<T>>, offset: MutableLiveData<Int>, callResponse: suspend () -> Response<List<T>>) {
		if (refresh) {
			offset.value = 0
		}
		var triedCount = 0
		var list: List<T> = if (refresh) emptyList() else data.value!!
		do {
			val new: List<T> = withContext(Dispatchers.IO) {
				val response = callResponse.invoke()
				if (response.isSuccessful) {
					response.body() ?: emptyList()
				} else {
					throw ResponseException(response.getHttpResponse())
				}
			}
			
			var count = 0
			if (new.isNotEmpty()) {
				for (item in new) {
					if (list.any { it.getDatabaseID() == item.getDatabaseID() }) {
						continue
					}
					list = list.plus(item)
					count++
				}
				offset.value = offset.value!!.plus(count)
			}
			data.value = list
		} while (new.isNotEmpty() && count <= RELOAD_THRESHOLD && triedCount++ <= MAX_TRY_COUNT)
	}
	
	@JvmStatic
	fun LifecycleOwner.loadAvatar(userID: String, setAction: (Bitmap?) -> Unit) {
		val application = MySpaceApplication.instance
		if (application.currentUser.value?.id == userID) {
			application.currentAvatar.observe(this) {
				setAction.invoke(it)
			}
		} else {
			val userInfo: MutableLiveData<UserInfo> by lazy { MutableLiveData() }
			val found = application.usersPool.findUserInfo(userID)
			if (found != null) {
				userInfo.value = found
			} else {
				application.findOrGetUserInfo(userID, {
					userInfo.value = application.usersPool.findUserInfo(userID)
				}, {
					userInfo.value = application.usersPool.findUserInfo(userID)
				}, this.lifecycleScope)
			}
			
			userInfo.observe(this) {
				setAction.invoke(it.avatar)
			}
		}
	}
	
	@JvmStatic
	fun defaultTransitionNavOption(): NavOptions {
		return NavOptions.Builder().apply {
			setEnterAnim(R.anim.from_right)
			setExitAnim(R.anim.to_left)
			setPopEnterAnim(R.anim.from_left)
			setPopExitAnim(R.anim.to_right)
		}.build()
	}
}
