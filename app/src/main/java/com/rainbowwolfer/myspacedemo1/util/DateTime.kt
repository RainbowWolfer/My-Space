//package com.rainbowwolfer.myspacedemo1.util
//
//import android.os.Parcelable
//import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.toDuoNumber
//import kotlinx.parcelize.Parcelize
//import org.joda.time.Days
//import java.util.*
//
//
//@Parcelize
//class DateTime(
//	val year: Int,
//	val month: Int,
//	val day: Int,
//	val hour: Int,
//	val minute: Int,
//	val second: Int,
//) : Parcelable {
//	constructor(calendar: Calendar) : this(
//		year = calendar.get(Calendar.YEAR),
//		month = (calendar.get(Calendar.MONTH) + 1),
//		day = calendar.get(Calendar.DAY_OF_MONTH),
//		hour = calendar.get(Calendar.HOUR_OF_DAY),
//		minute = calendar.get(Calendar.MINUTE),
//		second = calendar.get(Calendar.SECOND),
//	)
//
//	constructor(dateTimeString: String) : this(
//		year = kotlin.runCatching { dateTimeString.substring(0, 3).toInt() }.getOrNull() ?: -1,
//		month = kotlin.runCatching { dateTimeString.substring(5, 6).toInt() }.getOrNull() ?: -1,
//		day = kotlin.runCatching { dateTimeString.substring(8, 9).toInt() }.getOrNull() ?: -1,
//		hour = kotlin.runCatching { dateTimeString.substring(11, 12).toInt() }.getOrNull() ?: -1,
//		minute = kotlin.runCatching { dateTimeString.substring(14, 15).toInt() }.getOrNull() ?: -1,
//		second = kotlin.runCatching { dateTimeString.substring(17, 18).toInt() }.getOrNull() ?: -1,
//	)
//
//	fun isValid(): Boolean {
//		return when {
//			listOf(year, month, day, hour, minute, second).any { it <= 0 } -> false
//			month > 12 -> false
//			day > 31 -> false
//			hour > 24 -> false
//			minute > 60 -> false
//			second > 60 -> false
//			else -> true
//		}
//	}
//
//	fun convertToRecentFormat(): String {
//		if (!isValid()) {
//			return "ERROR"
//		}
//		val now = now()
//		//date
//
//		//time
//
//		return ""
//	}
//
//	fun calculateDayDiff(to: DateTime): Int {
//		val yearOffset = to.year - this.year
//		val monthOffset = to.month - this.month
//		val dayOffset = to.day - this.day
//
////		val days: Int = Days.daysBetween(date1, date2).getDays()
//		return 0
//	}
//
//	override fun toString(): String {
//		return "${year}-${month.toDuoNumber()}-${day.toDuoNumber()} ${hour.toDuoNumber()}:${minute.toDuoNumber()}:${second.toDuoNumber()}"
//	}
//
//	override fun equals(other: Any?): Boolean {
//		if (this === other) return true
//		if (javaClass != other?.javaClass) return false
//
//		other as DateTime
//
//		if (year != other.year) return false
//		if (month != other.month) return false
//		if (day != other.day) return false
//		if (hour != other.hour) return false
//		if (minute != other.minute) return false
//		if (second != other.second) return false
//
//		return true
//	}
//
//	override fun hashCode(): Int {
//		var result = year
//		result = 328 * result + month
//		result = 328 * result + day
//		result = 328 * result + hour
//		result = 328 * result + minute
//		result = 328 * result + second
//		return result
//	}
//
//	companion object {
//		fun now(): DateTime = DateTime(Calendar.getInstance())
//
//		fun Calendar.toDateTime(): DateTime = DateTime(this)
//		fun String.toDateTime(): DateTime = DateTime(this)
//
//		fun isLeapYear(year: Int): Boolean {
//			return if (year % 4 == 0) {
//				if (year % 100 == 0) {
//					year % 400 == 0
//				} else {
//					true
//				}
//			} else {
//				false
//			}
//		}
//
//		fun maxDayInMonth(month: Int, year: Int): Int {
//			return when (month) {
//				1 -> 31
//				2 -> if (isLeapYear(year)) 29 else 28
//				3 -> 31
//				4 -> 30
//				5 -> 31
//				6 -> 30
//				7 -> 31
//				8 -> 31
//				9 -> 30
//				10 -> 31
//				11 -> 30
//				12 -> 31
//				else -> -1
//			}
//		}
//	}
//}