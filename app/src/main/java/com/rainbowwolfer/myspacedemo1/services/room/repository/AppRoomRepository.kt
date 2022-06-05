package com.rainbowwolfer.myspacedemo1.services.room.repository

import androidx.annotation.WorkerThread
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.services.room.dao.DraftsDao
import kotlinx.coroutines.flow.Flow

class AppRoomRepository(
	private val dao: DraftsDao
) {
	val allDrafts: Flow<List<Draft>> = dao.getAll()
	
	@WorkerThread
	suspend fun insert(draft: Draft) {
		dao.insertAll(draft)
	}
	
	@WorkerThread
	suspend fun delete(draft: Draft) {
		dao.delete(draft)
	}
	
	@WorkerThread
	suspend fun update(draft: Draft) {
		dao.update(draft)
	}
	
}