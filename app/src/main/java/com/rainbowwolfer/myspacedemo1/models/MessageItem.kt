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
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		
		other as MessageItem
		
		if (id != other.id) return false
		if (content != other.content) return false
		if (datetime != other.datetime) return false
		if (isSelf != other.isSelf) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + datetime.hashCode()
		return result
	}
	
}