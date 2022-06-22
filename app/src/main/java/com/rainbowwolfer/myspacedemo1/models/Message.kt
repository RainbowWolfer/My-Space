package com.rainbowwolfer.myspacedemo1.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "messages")
data class Message(
	@SerializedName("ID") @PrimaryKey(autoGenerate = true) val id: Long,
	@SerializedName("SenderID") @ColumnInfo(name = "sender_id") val senderID: String,
	@SerializedName("ReceiverID") @ColumnInfo(name = "receiver_id") val receiverID: String,
	@SerializedName("DateTime") @ColumnInfo(name = "datetime") val dateTime: String,
	@SerializedName("TextContent") @ColumnInfo(name = "text_content") val textContent: String,
	@SerializedName("HasReceived") @ColumnInfo(name = "has_received") var hasReceived: Boolean,
) : Parcelable, DatabaseID<Long> {
	override fun getDatabaseID(): Long = id
}
