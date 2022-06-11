package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class NewPostVote(
	@SerializedName("post_id") val postID: String,
	@SerializedName("user_id") val userID: String,
	@SerializedName("cancel") val cancel: Boolean,
	@SerializedName("score") val score: Int,
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
)
