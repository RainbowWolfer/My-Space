package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class NewUsername(
	@SerializedName("id") val id: String,
	@SerializedName("username") val username: String,
	@SerializedName("password") val password: String,
	@SerializedName("new_username") val newUsername: String,
)