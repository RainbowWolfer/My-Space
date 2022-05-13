package com.rainbowwolfer.myspacedemo1.models

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
	@SerializedName("ID") override var id: String,
	@SerializedName("Username") var username: String,
	@SerializedName("Password") var password: String,
	@SerializedName("Email") var email: String,
	@SerializedName("ProfileDescription") val profileDescription: String
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<User> {
		@JvmStatic
		override fun generateDefault(): User {
			return User(
				EasyFunctions.generateRandomString(20),
				EasyFunctions.generateRandomString(10),
				EasyFunctions.generateRandomString(15),
				EasyFunctions.generateRandomString(10) + "@email.com",
				EasyFunctions.generateRandomString(40),
			)
		}
		
		@JvmStatic
		fun getTestLogUser(): User {
			return User(
				"1",
				"RainbowWolfer",
				"123456",
				"1519787190@qq.com",
				"This is just a simple description and nothing more. I do not know what to say anymore. Leave me alone.",
			)
		}
//
//		@JvmStatic
//		fun List<User>?.getUser(): User? {
//			if (this == null) {
//				return null
//			}
//			return if (this.isEmpty()) null else this[0]
//		}
//
//		var current: User? = null
//			set(value) {
//				field = value
//			}
//
//		val avatar: MutableLiveData<Bitmap> = MutableLiveData(null)
//
//		fun isLoggedIn() = current != null
//
//		fun User.compareID(user: User): Boolean {
//			return user.id == this.id
//		}
	}
}
