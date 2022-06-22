package com.rainbowwolfer.myspacedemo1.models.api

import com.google.gson.annotations.SerializedName

data class GetAppVersion(
	@SerializedName("Version") val version: String,
)