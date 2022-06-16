package com.rainbowwolfer.myspacedemo1.ui.fragments.main.collections

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.UserCollection

class CollectionsFragmentViewModel : ViewModel() {
	val collecitons: MutableLiveData<List<UserCollection>> by lazy { MutableLiveData(emptyList()) }
	
	val offset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
}