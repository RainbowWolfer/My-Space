package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
	@SerializedName("ID") val id: String,
	@SerializedName("UserID") val userID: String,
	@SerializedName("PostID") val postID: String,
	@SerializedName("TextContent") val textContent: String,
	@SerializedName("DateTime") val datetime: String,
	@SerializedName("Username") val username: String,
	@SerializedName("Email") val email: String,
	@SerializedName("Profile") val profile: String,
	@SerializedName("Upvotes") var upvotes: Int,
	@SerializedName("Downvotes") var downvotes: Int,
	//-1 -> none
	//0  -> down
	//1  -> up
	@SerializedName("Voted") var voted: Int,
) : Parcelable, DatabaseID<String> {
	companion object {
		const val VOTE_UP = 1
		const val VOTE_DOWN = 0
		const val VOTE_NONE = -1
	}
	
	fun getScore(): Int = upvotes - downvotes
	
	override fun getDatabaseID(): String = id
}
