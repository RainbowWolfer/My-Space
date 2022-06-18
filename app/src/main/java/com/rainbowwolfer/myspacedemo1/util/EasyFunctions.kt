package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
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
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Minutes
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
object EasyFunctions {
	private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
	
	fun generateRandomString(length: Int = 10): String {
		return (1..length)
			.map {
				Random.nextInt(0, charPool.size)
			}
			.map(charPool::get)
			.joinToString("")
	}
	
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
	
	fun convertMD5(input: String): String {
		val md = MessageDigest.getInstance("MD5")
		return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
	}
	
	fun String.toMD5(): String {
		return convertMD5(this)
	}
	
	fun Response<*>.getHttpResponse(): GoResponse {
		return try {
			val jsonResponse = JSONObject(this.errorBody()?.string().toString()).toString()
			Gson().fromJson(jsonResponse, GoResponse::class.java)
		} catch (ex: Exception) {
			ex.printStackTrace()
			GoResponse(0, "Http Response Json Convert Error", 0)
		}
	}
	
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
	
	fun defaultTransitionNavOption(): NavOptions {
		return NavOptions.Builder().apply {
			setEnterAnim(R.anim.from_right)
			setExitAnim(R.anim.to_left)
			setPopEnterAnim(R.anim.from_left)
			setPopExitAnim(R.anim.to_right)
		}.build()
	}
	
	fun Int.toDuo(): String {
		return when {
			this < 0 -> "$this"
			this < 10 -> "0$this"
			else -> "$this"
		}
	}
	
	fun String.toDateTime(): DateTime? {
		val str = this.trim()
		if (str.length != 19) {
			return null
		}
		val year = kotlin.runCatching { str.substring(0, 4).toInt() }.getOrNull() ?: return null
		val month = kotlin.runCatching { str.substring(5, 7).toInt() }.getOrNull() ?: return null
		val day = kotlin.runCatching { str.substring(8, 10).toInt() }.getOrNull() ?: return null
		val hour = kotlin.runCatching { str.substring(11, 13).toInt() }.getOrNull() ?: return null
		val minute = kotlin.runCatching { str.substring(14, 16).toInt() }.getOrNull() ?: return null
		val second = kotlin.runCatching { str.substring(17, 19).toInt() }.getOrNull() ?: return null
		println("$str $year $month $day $hour $minute $second")
		return DateTime(year, month, day, hour, minute, second)
	}
	
	fun DateTime.getDate(): String = "${this.year.toDuo()}-${this.monthOfYear.toDuo()}-${this.dayOfMonth.toDuo()}"
	fun DateTime.getTime(): String = "${this.hourOfDay.toDuo()}:${this.minuteOfHour.toDuo()}:${this.secondOfMinute.toDuo()}"
	
	fun String.convertToRecentFormat(context: Context): String {
		val now = DateTime.now()
		val from = this.toDateTime() ?: return context.getString(R.string.error)
		val date = when (Days.daysBetween(from, now).days) {
			0 -> context.getString(R.string.today)
			1 -> context.getString(R.string.yesterday)
			else -> from.getDate()
		}
		val minutes = Minutes.minutesBetween(from, now).minutes
		val time = when {
			minutes < 1 -> context.getString(R.string.just_now)
			minutes < 2 -> context.getString(R.string.min1)
			minutes < 5 -> context.getString(R.string.min5)
			minutes < 10 -> context.getString(R.string.min10)
			minutes < 30 -> context.getString(R.string.half_an_hour)
			minutes < 60 -> context.getString(R.string.an_hour)
			else -> from.getTime()
		}
		return "$date $time"
	}
}
