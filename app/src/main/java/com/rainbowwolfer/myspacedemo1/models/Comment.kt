package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize
import kotlin.collections.ArrayList
import kotlin.random.Random

@Parcelize
data class Comment(
	@SerializedName("ID") val id: String,
	@SerializedName("UserID") val userID: String,
	@SerializedName("PostID") val postID: String,
	@SerializedName("TextContent") val textContent: String,
	@SerializedName("DateTime") val datetime: String,
	@SerializedName("Upvotes") val upvotes: Int,
	@SerializedName("Downvotes") val downvotes: Int,
) : Parcelable, DatabaseID<String> {
	companion object {
		@JvmStatic
		fun getTestList(count: Int = 0): ArrayList<Comment> {
			val list = arrayListOf<Comment>()
			for (i in 0 until count) {
				list.add(
					Comment(
						i.toString(),
						EasyFunctions.generateRandomString(10),
						EasyFunctions.generateRandomString(10),
						EasyFunctions.generateRandomString(30),
						"Calendar.getInstance()",
						Random.nextInt(),
						Random.nextInt(),
					)
				)
			}
			return list
		}
	}
	
	override fun getDatabaseID(): String = id
}
