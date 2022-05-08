package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostResult(
	val publisherID: String,
	val content: String,
	val images: List<ByteArray?>,
	val visibility: PostVisibility,
	val replyLimit: PostVisibility,
	val tags: List<String>,
) : Parcelable