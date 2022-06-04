package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class NewComment(
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
	@SerializedName("post_id") val postID: String,
	@SerializedName("text_content") val textContent: String,
)