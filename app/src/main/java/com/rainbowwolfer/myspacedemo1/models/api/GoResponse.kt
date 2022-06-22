package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class GoResponse(
	@SerializedName("Code") val code: Int = 400,
	@SerializedName("Content") val content: String = "Not Initialized",
	@SerializedName("Error") val errorCode: Int = 0
)