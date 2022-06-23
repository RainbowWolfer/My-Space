package com.rainbowwolfer.myspacedemo1.models.flag

import com.google.gson.annotations.SerializedName

data class FlagMessage(
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
	@SerializedName("sender_id") val senderID: String,
	@SerializedName("flag_has_received") val flagHasReceived: Boolean,
)
