package com.rainbowwolfer.myspacedemo1.models.enums

import com.google.gson.annotations.SerializedName

enum class PostsLimit {
	@SerializedName("all")
	All,
	
	@SerializedName("hot")
	Hot,
	
	@SerializedName("random")
	Random,
	
	@SerializedName("following")
	Following
}