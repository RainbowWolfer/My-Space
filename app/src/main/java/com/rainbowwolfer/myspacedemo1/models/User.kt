package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
	override val id: String,
	val username: String,
	val profileDescription: String,
	val avatarBase64: String,
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<User> {
		@JvmStatic
		override fun generateDefault(): User {
			return User(
				EasyFunctions.generateRandomString(20),
				EasyFunctions.generateRandomString(10),
				EasyFunctions.generateRandomString(40),
				"",
			)
		}
		
		@JvmStatic
		fun getTestLogUser(): User {
			return User(
				"1",
				"RainbowWolfer",
				"This is just a simple description and nothing more. I do not know what to say anymore. Leave me alone.",
				""
			)
		}
		
		var current: User? = null
		
		fun isLoggedIn() = current != null
		
		fun User.compareID(user: User): Boolean {
			return user.id == this.id
		}
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as User
		
		if (id != other.id) return false
		if (username != other.username) return false
		if (profileDescription != other.profileDescription) return false
		if (avatarBase64 != other.avatarBase64) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + username.hashCode()
		result = 31 * result + profileDescription.hashCode()
		result = 31 * result + avatarBase64.hashCode()
		return result
	}
}
