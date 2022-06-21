package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "message_contactc")
data class MessageContact(
	@SerializedName("SenderID") @PrimaryKey(autoGenerate = true) val senderID: Long,
	@SerializedName("Username") @ColumnInfo("username") val username: String,
	@SerializedName("TextContent") @ColumnInfo("text_content") val textContent: String,
	@SerializedName("DateTime") @ColumnInfo("datetime") val dateTime: String,
	@SerializedName("HasUnread") @ColumnInfo("has_unread") val hasUnread: Boolean,
) : Parcelable, DatabaseID<Long> {
	override fun getDatabaseID(): Long = senderID
}