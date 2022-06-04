package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Comment

class PostDetailViewModel : ViewModel() {
	val comments: MutableLiveData<List<Comment>> by lazy { MutableLiveData() }
	
}