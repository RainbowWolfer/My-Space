package com.rainbowwolfer.myspacedemo1.models.records

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepostRecord(
	@SerializedName("post_id") val postID: String,
	@SerializedName("user_id") val userID: String,
	@SerializedName("username") val username: String,
	@SerializedName("time") val time: String,
	@SerializedName("quote") val quote: String,
) : Parcelable, DatabaseID<String> {
	override fun getDatabaseID(): String = postID
}