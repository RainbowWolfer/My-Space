package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import com.rainbowwolfer.myspacedemo1.services.callbacks.ArgsCallBack
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageSet(
	override val id: String,
	val sender: User,
	val messages: ArrayList<MessageItem>,
) : Parcelable, DatabaseID {
	companion object : GenerateDefault<MessageSet> {
		override fun generateDefault(): MessageSet {
			return MessageSet(
				EasyFunctions.generateRandomString(20),
				User.generateDefault(),
				EasyFunctions.generateRandomList(0, 30,
					object : ArgsCallBack<MessageItem> {
						override fun getArgs(): MessageItem {
							return MessageItem.generateDefault()
						}
					}
				)
			)
		}
	}
	
}