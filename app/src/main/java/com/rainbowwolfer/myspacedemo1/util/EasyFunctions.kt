package com.rainbowwolfer.myspacedemo1.util

import com.rainbowwolfer.myspacedemo1.services.callbacks.ArgsCallBack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class EasyFunctions {
	companion object {
		@JvmStatic
		private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
		
		@JvmStatic
		fun generateRandomString(length: Int = 10): String {
			return (1..length)
				.map {
					Random.nextInt(0, charPool.size)
				}
				.map(charPool::get)
				.joinToString("")
		}
		
		@JvmStatic
		fun <T> generateRandomList(
			minCount: Int, maxCount: Int, argsCallBack: ArgsCallBack<T>,
		): ArrayList<T> {
			val list = arrayListOf<T>()
			val min = if (minCount > maxCount) maxCount else minCount
			val count = Random.nextInt(min, maxCount)
			for (i in 0..count) {
				list.add(argsCallBack.getArgs())
			}
			return list
		}
		
		@JvmStatic
		fun formatDateTime(calendar: Calendar): String {
			val year = calendar.get(Calendar.YEAR)
			val month = calendar.get(Calendar.MONTH)
			val day = calendar.get(Calendar.DAY_OF_MONTH)
			
			val hour = calendar.get(Calendar.HOUR_OF_DAY)
			val minute = calendar.get(Calendar.MINUTE)
			val second = calendar.get(Calendar.SECOND)
			
			return "$year/$month/$day $hour:$minute:$second"
		}
	}
}