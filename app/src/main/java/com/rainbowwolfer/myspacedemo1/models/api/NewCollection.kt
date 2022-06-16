package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.enums.CollectionType

data class NewCollection(
	@SerializedName("target_id") val targetID: String,
	@SerializedName("type") val type: CollectionType,
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String,
)
