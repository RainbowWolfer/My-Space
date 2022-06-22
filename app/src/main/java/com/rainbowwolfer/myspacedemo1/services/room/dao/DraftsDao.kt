package com.rainbowwolfer.myspacedemo1.services.room.dao

import androidx.room.*
import com.rainbowwolfer.myspacedemo1.models.Draft
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftsDao {
	@Query("select * from drafts")
	fun getAll(): Flow<List<Draft>>
	
	@Query("select * from drafts where user_id = :user_id")
	fun getAllByUser(user_id: String): Flow<List<Draft>>
	
	@Insert
	suspend fun insertAll(vararg drafts: Draft)
	
	@Update(entity = Draft::class)
	suspend fun update(draft: Draft)
	
	@Delete
	suspend fun delete(draft: Draft)
	
	@Query("DELETE FROM drafts")
	suspend fun deleteAll()
}