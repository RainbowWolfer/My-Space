package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class NewVote(
	@SerializedName("post_id") val postID: String,
	@SerializedName("user_id") val userID: String,
	@SerializedName("cancel") val cancel: Boolean,
	@SerializedName("score") val score: Int,
)
