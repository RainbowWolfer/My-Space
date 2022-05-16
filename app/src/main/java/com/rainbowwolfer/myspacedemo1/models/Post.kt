package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.toFormatDateTime
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Parcelize
data class Post(
	@SerializedName("ID") override val id: String,
	@SerializedName("PublisherID") val publisherID: String,
	@SerializedName("PublishDate") val publishDateTime: String,
	@SerializedName("EditDate") val editDateTime: String,
	@SerializedName("EditTimes") val editTiems: Int,
	@SerializedName("TextContent") val textContent: String,
	@SerializedName("Deleted") val deleted: Boolean,
	@SerializedName("ImagesCount") val imagesCount: Int,
	@SerializedName("Tags") val tags: String,
	@SerializedName("Upvotes") val upvotes: Int,
	@SerializedName("Downvotes") val downvotes: Int,
	@SerializedName("Repost") val repost: Int,
	@SerializedName("Comment") val comment: Int,
	@SerializedName("Visibility") val visibility: PostVisibility = PostVisibility.All,
	@SerializedName("Reply") val replyLimit: PostVisibility = PostVisibility.All,
	@SerializedName("IsRepost") val isRepost: Boolean,
	@SerializedName("OriginPostID") val originID: String,
	@SerializedName("ReposterID") val reposterID: String,
) : Parcelable, DatabaseID {
	fun getScore(): Int = upvotes - downvotes
	
	companion object : GenerateDefault<Post> {
		@JvmStatic
		override fun generateDefault(): Post {
			return Post(
				EasyFunctions.generateRandomString(20),
				EasyFunctions.generateRandomString(20),
				Calendar.getInstance().toFormatDateTime(),
				Calendar.getInstance().toFormatDateTime(),
				Random.nextInt(),
				EasyFunctions.generateRandomString(Random.nextInt(100, 500)),
				Random.nextBoolean(),
				Random.nextInt(9),
				EasyFunctions.generateRandomString(20),
				Random.nextInt(),
				Random.nextInt(),
				Random.nextInt(),
				Random.nextInt(),
				PostVisibility.All,
				PostVisibility.All,
				false,
				"",
				"",
			)
		}
	}
	
}