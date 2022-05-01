package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
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
	val publisher: User,
	val publishDateTime: Calendar,
	val content: String,
	val attachedImagesBase64: ArrayList<String>,
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<Post> {
		@JvmStatic
		override fun generateDefault(): Post {
			return Post(
				EasyFunctions.generateRandomString(20),
				User.generateDefault(),
				Calendar.getInstance(),
				EasyFunctions.generateRandomString(Random.nextInt(100, 500)),
				arrayListOf()
			)
		}
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as Post
		
		if (id != other.id) return false
		if (publisher != other.publisher) return false
		if (publishDateTime != other.publishDateTime) return false
		if (content != other.content) return false
		if (attachedImagesBase64 != other.attachedImagesBase64) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + publisher.hashCode()
		result = 31 * result + publishDateTime.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + attachedImagesBase64.hashCode()
		return result
	}
	
}