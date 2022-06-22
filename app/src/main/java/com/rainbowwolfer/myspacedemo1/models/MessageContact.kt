package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "message_contacts")
data class MessageContact(
	@SerializedName("SenderID") @PrimaryKey(autoGenerate = true) val senderID: Long,
	@SerializedName("Username") @ColumnInfo("username") var username: String,
	@SerializedName("TextContent") @ColumnInfo("text_content") var textContent: String,
	@SerializedName("DateTime") @ColumnInfo("datetime") var dateTime: String,
	@SerializedName("UnreadCount") @ColumnInfo("unread_count") var unreadCount: Int,
) : Parcelable, DatabaseID<Long> {
	override fun getDatabaseID(): Long = senderID
}