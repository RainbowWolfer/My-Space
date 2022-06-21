package com.rainbowwolfer.myspacedemo1.services.room.repository

import androidx.annotation.WorkerThread
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.services.room.dao.DraftsDao
import com.rainbowwolfer.myspacedemo1.services.room.dao.MessageContactsDao
import com.rainbowwolfer.myspacedemo1.services.room.dao.MessagesDao
import kotlinx.coroutines.flow.Flow

class AppRoomRepository(
	private val dao_draft: DraftsDao,
	private val dao_message: MessagesDao,
	private val dao_contact: MessageContactsDao,
) {
	val allDrafts: Flow<List<Draft>> = dao_draft.getAll()
	val allMessages: Flow<List<Message>> = dao_message.getAll()
	val allContacts: Flow<List<MessageContact>> = dao_contact.getAll()
	
	//message
	
	//message contacts
	
	//draft
	@WorkerThread
	suspend fun insertDraft(draft: Draft) {
		dao_draft.insertAll(draft)
	}
	
	@WorkerThread
	suspend fun deleteDraft(draft: Draft) {
		dao_draft.delete(draft)
	}
	
	@WorkerThread
	suspend fun updateDraft(draft: Draft) {
		dao_draft.update(draft)
	}
	
}