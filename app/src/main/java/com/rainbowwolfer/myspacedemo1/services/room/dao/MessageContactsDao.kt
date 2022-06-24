package com.rainbowwolfer.myspacedemo1.services.room.dao

import androidx.room.*
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageContactsDao {
	@Query("select * from message_contacts")
	fun getAll(): Flow<List<MessageContact>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(vararg contacts: MessageContact)
	
	@Update(entity = MessageContact::class)
	suspend fun update(contact: MessageContact)
	
	@Query("DELETE FROM message_contacts")
	suspend fun deleteAll()
}