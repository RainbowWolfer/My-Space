package com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.room.repository.AppRoomRepository
import kotlinx.coroutines.launch

class DraftsFragmentViewModel : ViewModel() {
	
	private val repository: AppRoomRepository = MySpaceApplication.instance.roomRepository
	
	val allDrafts: LiveData<List<Draft>> by lazy { repository.allDrafts.asLiveData() }
	
	fun insert(word: Draft) = viewModelScope.launch {
		repository.insert(word)
	}
	
	fun getSearched(content: String): List<Draft> {
		return allDrafts.value?.filter {
			it.textContent.contains(content, true)
		} ?: emptyList()
	}
}