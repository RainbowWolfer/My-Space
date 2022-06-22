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
	fun getMessagesBySenderID(user_id: String): Flow<List<Message>> {
		return dao_message.getBySender(user_id)
	}
	
	@WorkerThread
	suspend fun insertMessages(vararg message: Message) {
		dao_message.insertAll(*message)
	}
	
	@WorkerThread
	suspend fun updateMessage(message: Message) {
		dao_message.update(message)
	}
	
	//message contacts
	@WorkerThread
	suspend fun insertContacts(vararg contacts: MessageContact) {
		dao_contact.insertAll(*contacts)
	}
	
	@WorkerThread
	suspend fun updateContact(contact: MessageContact) {
		dao_contact.update(contact)
	}
	
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