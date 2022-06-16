package com.rainbowwolfer.myspacedemo1.models.enums

import com.google.gson.annotations.SerializedName

enum class CollectionType {
	@SerializedName("POST")
	Post,
	
	@SerializedName("MESSAGE")
	Message
}