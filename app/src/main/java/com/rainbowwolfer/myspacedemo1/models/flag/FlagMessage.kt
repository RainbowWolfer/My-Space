package com.rainbowwolfer.myspacedemo1.models.flag

import com.google.gson.annotations.SerializedName

data class FlagMessage(
	@SerializedName("Email") val email: String,
	@SerializedName("Password") val password: String,
	@SerializedName("SenderID") val senderID: String,
	@SerializedName("FlagHasReceived") val flagHasReceived: Boolean,
)
