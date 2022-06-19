package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class DeletePost(
	@SerializedName("post_id") val postID: String,
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
)