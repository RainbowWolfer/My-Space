package com.rainbowwolfer.myspacedemo1.util

import com.google.common.truth.Truth.assertThat
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.toDuo
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.toMD5
import org.junit.Assert
import org.junit.Test

class EasyFunctionsTest {
	@Test
	fun generateRandomString() {
		assertThat(EasyFunctions.generateRandomString()).isNotNull()
	}
	
	@Test
	fun convertMD5() {
		Assert.assertEquals(
			"My Space MD5".toMD5(),
			"11300454653aeffdcd2e081f4e11a78d"
		)
	}
	
	@Test
	fun toDuo() {
		Assert.assertEquals(5.toDuo(), "05")
		Assert.assertEquals(1.toDuo(), "01")
		Assert.assertEquals((-5).toDuo(), "-5")
		Assert.assertEquals(32.toDuo(), "32")
	}
}