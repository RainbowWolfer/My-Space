package com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.models.MessageContact

class MessageFragmentViewModel : ViewModel() {
	val messages: MutableLiveData<List<Message>> by lazy { MutableLiveData() }
	val contacts: MutableLiveData<List<MessageContact>> by lazy { MutableLiveData() }
	val offset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	
}