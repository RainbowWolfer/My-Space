package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.toDuo
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Minutes

@Suppress("MemberVisibilityCanBePrivate")
object DateTimeUtils {
	fun String.toDateTime(): DateTime? {
		val str = this.trim()
		if (str.length != 19) {
			return null
		}
		val year = kotlin.runCatching { str.substring(0, 4).toInt() }.getOrNull() ?: return null
		val month = kotlin.runCatching { str.substring(5, 7).toInt() }.getOrNull() ?: return null
		val day = kotlin.runCatching { str.substring(8, 10).toInt() }.getOrNull() ?: return null
		val hour = kotlin.runCatching { str.substring(11, 13).toInt() }.getOrNull() ?: return null
		val minute = kotlin.runCatching { str.substring(14, 16).toInt() }.getOrNull() ?: return null
		val second = kotlin.runCatching { str.substring(17, 19).toInt() }.getOrNull() ?: return null
		println("$str $year $month $day $hour $minute $second")
		return DateTime(year, month, day, hour, minute, second)
	}
	
	fun DateTime.getDate(): String = "${this.year.toDuo()}-${this.monthOfYear.toDuo()}-${this.dayOfMonth.toDuo()}"
	fun DateTime.getTime(): String = "${this.hourOfDay.toDuo()}:${this.minuteOfHour.toDuo()}:${this.secondOfMinute.toDuo()}"
	fun DateTime.getDateTime(): String = "${this.getDate()} ${this.getTime()}"
	
	fun String.convertToRecentFormat(context: Context): String {
		val now = DateTime.now()
		val from = this.toDateTime() ?: return context.getString(R.string.error)
		val date = when (Days.daysBetween(from, now).days) {
			0 -> context.getString(R.string.today)
			1 -> context.getString(R.string.yesterday)
			else -> from.getDate()
		}
		val minutes = Minutes.minutesBetween(from, now).minutes
		val time = when {
			minutes < 1 -> context.getString(R.string.just_now)
			minutes < 2 -> context.getString(R.string.min1)
			minutes < 5 -> context.getString(R.string.min5)
			minutes < 10 -> context.getString(R.string.min10)
			minutes < 30 -> context.getString(R.string.half_an_hour)
			minutes < 60 -> context.getString(R.string.an_hour)
			else -> from.getTime()
		}
		return "$date $time"
	}
}