package com.rainbowwolfer.myspacedemo1.models.records

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScoreRecord(
	@SerializedName("like_id") val likeID: String,
	@SerializedName("user_id") val userID: String,
	@SerializedName("username") val username: String,
	@SerializedName("time") val time: String,
	@SerializedName("vote") val voted: Int,
) : Parcelable, DatabaseID<String> {
	override fun getDatabaseID(): String = likeID
	fun isVoted(): Boolean? {
		return when (voted) {
			Post.VOTE_NONE -> null
			Post.VOTE_DOWN -> false
			Post.VOTE_UP -> true
			else -> null
		}
	}
}
