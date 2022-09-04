package com.rainbowwolfer.myspacedemo1.services

import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator.Companion.hasDigit
import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator.Companion.hasLowerCase
import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator.Companion.hasSpecialChar
import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator.Companion.hasUpperCase
import org.junit.Assert.*

import org.junit.Test

class PasswordStrengthCalculatorTest {
	
	@Test
	fun `hasLowerCase$app_debug`() {
		assertEquals("".hasLowerCase(),false)
		assertEquals("a".hasLowerCase(),true)
		assertEquals("A".hasLowerCase(),false)
		assertEquals("123d".hasLowerCase(),true)
	}
	
	@Test
	fun `hasUpperCase$app_debug`() {
		assertEquals("".hasUpperCase(),false)
		assertEquals("a".hasUpperCase(),false)
		assertEquals("A".hasUpperCase(),true)
		assertEquals("123d".hasUpperCase(),false)
		assertEquals("EWQ2".hasUpperCase(),true)
	}
	
	@Test
	fun `hasDigit$app_debug`() {
		assertEquals("".hasDigit(),false)
		assertEquals("a".hasDigit(),false)
		assertEquals("A".hasDigit(),false)
		assertEquals("123d".hasDigit(),true)
		assertEquals("EWQ2".hasDigit(),true)
	}
	
	@Test
	fun `hasSpecialChar$app_debug`() {
		assertEquals("".hasSpecialChar(),false)
		assertEquals("a".hasSpecialChar(),false)
		assertEquals(".wqe".hasSpecialChar(),true)
		assertEquals("!ejwqio".hasSpecialChar(),true)
		assertEquals("@".hasSpecialChar(),true)
	}
}