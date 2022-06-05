package com.rainbowwolfer.myspacedemo1.models

import android.net.Uri
import android.os.Parcelable
import android.text.TextUtils
import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.models.interfaces.GenerateDefault
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "drafts")
data class Draft(
	@PrimaryKey(autoGenerate = true) val id: Long,
	@ColumnInfo(name = "user_id") val userID: String,
	@ColumnInfo(name = "added_datetime") val addedDateTime: String,
	@ColumnInfo(name = "text_content") val textContent: String,
	@ColumnInfo(name = "post_visibility") val postVisibility: PostVisibility,
	@ColumnInfo(name = "reply_limit") val replyLimit: PostVisibility,
	@ColumnInfo(name = "tags") val tags: String,
	@ColumnInfo(name = "images_uri") val imagesURI: String,
	//file:///storage/emulated/0/DCIM/Camera/IMG_20220604_232659137.jpg
) : Parcelable, DatabaseID<Long> {
	companion object : GenerateDefault<Draft> {
		private const val SEPARATOR_TAGS = ","
		private const val SEPARATOR_URIS = "{&#E10;}"
		
		fun convertTags(tags: List<String>): String {
			return tags.joinToString(SEPARATOR_TAGS)
		}
		
		fun convertImagesURI(uris: List<Uri>): String {
			return uris.joinToString(SEPARATOR_URIS)
		}
		
		override fun generateDefault(): Draft {
			return Draft(
				0,
				"1",
				"2022-06-06 21:21:21",
				"Hello World",
				PostVisibility.All,
				PostVisibility.All,
				"good,example",
				"",
			)
		}
	}
	
	fun getImagesUri(): List<Uri> {
		val result = arrayListOf<Uri>()
		imagesURI.split(SEPARATOR_URIS).forEach {
			result.add(it.toUri())
		}
		return result.filter { !TextUtils.isEmpty(it.toString()) }
	}
	
	fun getTagsList(): List<String> {
		val result = arrayListOf<String>()
		tags.split(SEPARATOR_TAGS).forEach {
			result.add(it)
		}
		return result.filter { !TextUtils.isEmpty(it) }
	}
	
	override fun getDatabaseID(): Long = id
}