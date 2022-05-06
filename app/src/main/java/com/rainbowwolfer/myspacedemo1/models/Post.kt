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
	
}