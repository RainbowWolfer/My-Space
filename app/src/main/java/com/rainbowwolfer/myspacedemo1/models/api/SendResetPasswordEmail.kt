package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class SendResetPasswordEmail(
	@SerializedName("email") val email: String,
)