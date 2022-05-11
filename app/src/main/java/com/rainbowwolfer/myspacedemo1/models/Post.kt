package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Parcelize
data class Post(
	override val id: String,
	val publisherID: String,
	val publishDateTime: Calendar,
	val editDateTime: Calendar,
	val editTiems: Int,
	val textContent: String,
	val deleted: Boolean,
	val imagesCount: Int,
	val tags: String,
	val upvotes: Int,
	val downvotes: Int,
	val repost: Int,
	val visibility: PostVisibility,
	val replyLimit: PostVisibility,
	val isRepost: Boolean,
	val originID: String,
	val reposterID: String,
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<Post> {
		@JvmStatic
		override fun generateDefault(): Post {
			return Post(
				EasyFunctions.generateRandomString(20),
				EasyFunctions.generateRandomString(20),
				Calendar.getInstance(),
				Calendar.getInstance(),
				Random.nextInt(),
				EasyFunctions.generateRandomString(Random.nextInt(100, 500)),
				Random.nextBoolean(),
				Random.nextInt(9),
				EasyFunctions.generateRandomString(20),
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