package com.rainbowwolfer.myspacedemo1.services.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageContactsDao {
	@Query("select * from message_contactc")
	fun getAll(): Flow<List<MessageContact>>
}