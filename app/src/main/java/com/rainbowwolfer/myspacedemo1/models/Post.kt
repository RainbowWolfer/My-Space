package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
	//essential
	@SerializedName("ID") val id: String,
	@SerializedName("PublisherID") val publisherID: String,
	@SerializedName("PublishDate") val publishDateTime: String,
	@SerializedName("EditDate") val editDateTime: String,
	@SerializedName("EditTimes") val editTimes: Int,
	@SerializedName("TextContent") val textContent: String,
	@SerializedName("Deleted") val deleted: Boolean,
	@SerializedName("ImagesCount") val imagesCount: Int,
	@SerializedName("Tags") val tags: String,
	@SerializedName("Visibility") val visibility: PostVisibility = PostVisibility.All,
	@SerializedName("Reply") val replyLimit: PostVisibility = PostVisibility.All,
	@SerializedName("IsRepost") val isRepost: Boolean,
	@SerializedName("OriginPostID") val originPostID: String,
	@SerializedName("Upvotes") var upvotes: Int,
	@SerializedName("Downvotes") var downvotes: Int,
	@SerializedName("Comments") var comments: Int,
	@SerializedName("Reposts") var reposts: Int,
	@SerializedName("PublisherUsername") val publisherUsername: String,
	@SerializedName("PublisherEmail") val publisherEmail: String,
	@SerializedName("PublisherProfile") val publisherProfile: String,
	//-----------
	//if repost
	@SerializedName("OriginUserID") val originUserID: String? = null,
	@SerializedName("OriginUserUsername") val originUserUsername: String? = null,
	@SerializedName("OriginUserEmail") val originUserEmail: String? = null,
	@SerializedName("OriginUserProfile") val originUserProfile: String? = null,
	@SerializedName("OriginPublishDate") val originPublishDate: String? = null,
	@SerializedName("OriginEditDate") val originEditDate: String? = null,
	@SerializedName("OriginEditTimes") val originEditTimes: Int? = null,
	@SerializedName("OriginTextContent") val originTextContent: String? = null,
	@SerializedName("OriginDeleted") val originDeleted: Boolean? = null,
	@SerializedName("OriginImagesCount") val originImagesCount: Int? = null,
	@SerializedName("OriginTags") val originTags: String? = null,
	@SerializedName("OriginVisibility") val originVisibility: PostVisibility? = null,
	@SerializedName("OriginReply") val originReply: PostVisibility? = null,
	@SerializedName("OriginIsRepost") val originIsRepost: Boolean? = null,
	@SerializedName("OriginOriginPostID") val originOriginPostID: String? = null,
	@SerializedName("OriginUpvotes") val originUpvotes: Int? = null,
	@SerializedName("OriginDownvotes") val originDownvotes: Int? = null,
	@SerializedName("OriginComments") val originComments: Int? = null,
	@SerializedName("OriginReposts") val originReposts: Int? = null,
	//------------
	//Post Properties
	@SerializedName("Score") val score: Int,//upvotes - downvotes
	@SerializedName("HasReposted") val hasReposted: Boolean,
	//-1 -> none
	//0  -> down
	//1  -> up
	@SerializedName("Voted") var voted: Int,//whether user had voted (depends on query)
	//------------
	//if respost (Origin Post Properties)
	@SerializedName("OriginScore") val originScore: Int? = null,
	@SerializedName("OriginVoted") val originVoted: Int? = null,
//	@SerializedName("OriginHasReposted") val originHasReposted: Boolean? = null,
) : Parcelable, DatabaseID<String> {
	companion object {
		const val VOTE_UP = 1
		const val VOTE_DOWN = 0
		const val VOTE_NONE = -1
	}
	
	override fun getDatabaseID(): String = id
	
	fun isVoted(): Boolean? {
		return when (voted) {
			VOTE_NONE -> null
			VOTE_DOWN -> false
			VOTE_UP -> true
			else -> null
		}
	}
	
	fun getPublisher(): User {
		return User(
			id = publisherID,
			username = publisherUsername,
			email = publisherEmail,
			profileDescription = publisherProfile,
		)
	}
	
	fun getOriginUser(): User? {
		return if (!isRepost) {
			null
		} else {
			User(
				id = originUserID!!,
				username = originUserUsername!!,
				email = originUserEmail!!,
				profileDescription = originUserProfile!!,
			)
		}
	}
	
	fun getOriginPost(): Post? {
		return if (!isRepost) {
			null
		} else {
			Post(
				id = originPostID,
				publisherID = originUserID!!,
				publishDateTime = originPublishDate!!,
				editDateTime = originEditDate!!,
				editTimes = originEditTimes!!,
				textContent = originTextContent!!,
				deleted = originDeleted!!,
				imagesCount = originImagesCount!!,
				tags = originTags!!,
				visibility = originVisibility!!,
				replyLimit = originReply!!,
				isRepost = originIsRepost!!,
				originPostID = originOriginPostID!!,
				upvotes = originUpvotes!!,
				downvotes = originDownvotes!!,
				comments = originComments!!,
				reposts = originReposts!!,
				publisherUsername = originUserUsername!!,
				publisherEmail = originUserEmail!!,
				publisherProfile = originUserProfile!!,
				score = originScore!!,
				voted = originVoted!!,
				hasReposted = hasReposted,
			)
		}
	}
	
}