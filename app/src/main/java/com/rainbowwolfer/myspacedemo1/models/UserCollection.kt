package com.rainbowwolfer.myspacedemo1.models

import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.enums.CollectionType
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID

data class UserCollection(
	@SerializedName("ID") val id: String,
	@SerializedName("UserID") val userID: String,
	@SerializedName("TargetID") val targetID: String,
	@SerializedName("Type") val type: CollectionType,
	@SerializedName("Time") val time: String,
	@SerializedName("PublisherID") val publisherID: String,
	@SerializedName("PublisherUsername") val publisherUsername: String,
	@SerializedName("TextContent") val textContent: String,
	@SerializedName("ImagesCount") val imagesCount: Int,
	@SerializedName("IsRepost") val isRepost: Boolean,
) : DatabaseID<String> {
	override fun getDatabaseID(): String = id
}
