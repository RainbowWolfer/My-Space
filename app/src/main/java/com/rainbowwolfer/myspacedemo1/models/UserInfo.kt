package com.rainbowwolfer.myspacedemo1.models

import android.graphics.Bitmap

class UserInfo(
	val id: String,
	var user: User?,
	var avatar: Bitmap?,
) {
	companion object {
		@JvmStatic
		fun ArrayList<UserInfo>.findUserInfo(id: String): UserInfo? {
			return this.find { it.id == id }
		}
		
		@JvmStatic
		fun ArrayList<UserInfo>.findUser(id: String): User? {
			return findUserInfo(id)?.user
		}
		
		@JvmStatic
		fun ArrayList<UserInfo>.findAvatar(id: String): Bitmap? {
			return findUserInfo(id)?.avatar
		}
		
		@JvmStatic
		fun ArrayList<UserInfo>.addUser(user: User) {
			val found = findUserInfo(user.id)
			if (found != null) {
				found.user = user
			} else {
				this.add(UserInfo(user.id, user, null))
			}
		}
		
		@JvmStatic
		fun ArrayList<UserInfo>.updateAvatar(id: String, bitmap: Bitmap?) {
			val found = findUserInfo(id)
			if (found != null) {
				found.avatar = bitmap
			} else {
				this.add(UserInfo(id, null, bitmap))
			}
		}
		
		
	}
}