package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class SignUpInfo(
	@SerializedName("username") val username: String,
	@SerializedName("password") val password: String,
	@SerializedName("email") val email: String,
)