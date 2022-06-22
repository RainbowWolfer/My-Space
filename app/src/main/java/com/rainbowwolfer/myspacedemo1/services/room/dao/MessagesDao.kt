package com.rainbowwolfer.myspacedemo1.services.room.dao

import androidx.room.*
import com.rainbowwolfer.myspacedemo1.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {
	@Query("select * from messages")
	fun getAll(): Flow<List<Message>>
	
	@Query("select * from messages where sender_id = :user_id order by datetime desc")
	fun getBySender(user_id: String): Flow<List<Message>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(vararg message: Message)
	
	@Update(entity = Message::class)
	suspend fun update(message: Message)
}