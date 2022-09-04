package com.rainbowwolfer.myspacedemo1.util

import com.rainbowwolfer.myspacedemo1.util.DateTimeUtils.convertToRecentFormat
import com.rainbowwolfer.myspacedemo1.util.DateTimeUtils.getDate
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DateTimeUtilsTest {
	
	@Test
	fun getDate() {
		assertEquals(DateTime.now().getDate(), "2022-06-27")
	}
	
}