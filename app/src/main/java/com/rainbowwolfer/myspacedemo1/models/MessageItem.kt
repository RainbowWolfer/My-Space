package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.random.Random

@Parcelize
data class MessageItem(
	override val id: String,
	val content: String,
	val datetime: Calendar,
	val isSelf: Boolean,
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<MessageItem> {
		override fun generateDefault(): MessageItem {
			return MessageItem(
				EasyFunctions.generateRandomString(20),
				EasyFunctions.generateRandomString(Random.nextInt(20, 200)),
				Calendar.getInstance(),
				Random.nextBoolean()
			)
		}
	}
	
}