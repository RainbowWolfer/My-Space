package com.rainbowwolfer.myspacedemo1.services

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.R
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordStrengthCalculator : TextWatcher {
	
	var strengthLevel: MutableLiveData<StrengthLevel> = MutableLiveData()
	var strengthColor: MutableLiveData<Int> = MutableLiveData()
	
	var lowerCase: MutableLiveData<Int> = MutableLiveData(0)
	var upperCase: MutableLiveData<Int> = MutableLiveData(0)
	var digit: MutableLiveData<Int> = MutableLiveData(0)
	var specialChar: MutableLiveData<Int> = MutableLiveData(0)
	
	var count: MutableLiveData<Int> = MutableLiveData()
	
	override fun afterTextChanged(editable: Editable?) {
		count.value = editable.toString().length
	}
	
	override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
	
	}
	
	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		if (s != null) {
			lowerCase.value = if (s.hasLowerCase()) 1 else 0
			upperCase.value = if (s.hasUpperCase()) 1 else 0
			digit.value = if (s.hasDigit()) 1 else 0
			specialChar.value = if (s.hasSpecialChar()) 1 else 0
			calculateStrength(s)
		}
	}
	
	private fun calculateStrength(password: CharSequence) {
		if (password.length in 0..6) {
			strengthColor.value = R.color.weak
			strengthLevel.value = StrengthLevel.WEAK
		} else if (password.length in 7..9) {
			if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1) {
				strengthColor.value = R.color.medium
				strengthLevel.value = StrengthLevel.MEDIUM
			}
		} else if (password.length in 10..14) {
			if (lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || specialChar.value == 1) {
				if (lowerCase.value == 1 && upperCase.value == 1) {
					strengthColor.value = R.color.strong
					strengthLevel.value = StrengthLevel.STRONG
				}
			}
		} else if (password.length > 14) {
			if (lowerCase.value == 1 && upperCase.value == 1 && digit.value == 1 && specialChar.value == 1) {
				strengthColor.value = R.color.bulletproof
				strengthLevel.value = StrengthLevel.BULLETPROOF
			}
		}
	}
	
	private fun CharSequence.hasLowerCase(): Boolean {
		val pattern: Pattern = Pattern.compile("[a-z]")
		val hasLowerCase: Matcher = pattern.matcher(this)
		return hasLowerCase.find()
	}
	
	private fun CharSequence.hasUpperCase(): Boolean {
		val pattern: Pattern = Pattern.compile("[A-Z]")
		val hasUpperCase: Matcher = pattern.matcher(this)
		return hasUpperCase.find()
	}
	
	private fun CharSequence.hasDigit(): Boolean {
		val pattern: Pattern = Pattern.compile("[0-9]")
		val hasDigit: Matcher = pattern.matcher(this)
		return hasDigit.find()
	}
	
	private fun CharSequence.hasSpecialChar(): Boolean {
		val pattern: Pattern = Pattern.compile("[!@#$%^&*()_=+{}/.<>|\\[\\]~-]")
		val hasSpecialChar: Matcher = pattern.matcher(this)
		return hasSpecialChar.find()
	}
}