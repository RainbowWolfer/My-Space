package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.Post

class PostDetailViewModel : ViewModel() {
	val comments: MutableLiveData<List<Comment>> by lazy { MutableLiveData() }
	val post: MutableLiveData<Post> by lazy { MutableLiveData() }
}