package com.rainbowwolfer.myspacedemo1.services.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.rainbowwolfer.myspacedemo1.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
	@Query("select * from messages")
	fun getAll(): Flow<List<Message>>
}