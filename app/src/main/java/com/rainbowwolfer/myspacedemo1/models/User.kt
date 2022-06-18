package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
	@SerializedName("ID") var id: String,
	@SerializedName("Username") var username: String,
	@SerializedName("Password") var password: String = "",
	@SerializedName("Email") var email: String,
	@SerializedName("ProfileDescription") val profileDescription: String,
	@SerializedName("IsFollowing") var isFollowing: Boolean,
) : Parcelable, DatabaseID<String> {
	companion object : GenerateDefault<User> {
		
		override fun generateDefault(): User {
			return User(
				id = EasyFunctions.generateRandomString(20),
				username = EasyFunctions.generateRandomString(10),
				password = EasyFunctions.generateRandomString(15),
				email = EasyFunctions.generateRandomString(10) + "@email.com",
				profileDescription = EasyFunctions.generateRandomString(40),
				isFollowing = false
			)
		}
		
	}
	
	override fun getDatabaseID(): String = id
}
