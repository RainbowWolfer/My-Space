package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.UserInfo

class UserProfileViewModel : ViewModel() {
	val userInfo: MutableLiveData<UserInfo?> by lazy { MutableLiveData(null) }
	
}