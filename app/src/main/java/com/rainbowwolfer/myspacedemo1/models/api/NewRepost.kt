package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility

//no images on repost
data class NewRepost(
	@SerializedName("origin_post_id") val originPostID: String,
	@SerializedName("publisher_id") val publisherID: String,
	@SerializedName("text_content") val textContent: String,
	@SerializedName("post_visibility") val postVisibility: PostVisibility,
	@SerializedName("reply_limit") val replyLimit: PostVisibility,
	@SerializedName("tags") val tags: List<String>,
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
)