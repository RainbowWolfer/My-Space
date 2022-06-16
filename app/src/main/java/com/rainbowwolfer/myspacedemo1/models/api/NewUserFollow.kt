package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class NewUserFollow(
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
	@SerializedName("target_id") val targetID: String,
	@SerializedName("cancel") val cancel: Boolean,
)