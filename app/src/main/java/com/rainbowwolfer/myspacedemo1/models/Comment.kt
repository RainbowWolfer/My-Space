package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Parcelize
data class Comment(
	override val id: String,
	val userID: String,
	val postID: String,
	val textContent: String,
	val datetime: Calendar,
	val upvotes: Int,
	val downvotes: Int,
) : Parcelable, DatabaseID {
	companion object {
		@JvmStatic
		fun getTestList(count: Int = 15): ArrayList<Comment> {
			val list = arrayListOf<Comment>()
			for (i in 0..count) {
				list.add(
					Comment(
						i.toString(),
						EasyFunctions.generateRandomString(10),
						EasyFunctions.generateRandomString(10),
						EasyFunctions.generateRandomString(30),
						Calendar.getInstance(),
						Random.nextInt(),
						Random.nextInt(),
					)
				)
			}
			return list
		}
	}
}
