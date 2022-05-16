package com.rainbowwolfer.myspacedemo1.models.enums

import com.google.gson.annotations.SerializedName

enum class PostVisibility {
	@SerializedName("all")
	All,
	@SerializedName("follower")
	Follower,
	@SerializedName("none")
	None
}