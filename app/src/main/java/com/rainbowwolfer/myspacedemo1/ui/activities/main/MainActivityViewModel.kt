package com.rainbowwolfer.myspacedemo1.ui.activities.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit

class MainActivityViewModel : ViewModel() {
	val postsLimit: MutableLiveData<PostsLimit> by lazy { MutableLiveData(PostsLimit.All) }
	val posts: MutableLiveData<List<Post>> by lazy { MutableLiveData(emptyList()) }
	
	val lastViewPosiiton: MutableLiveData<Int> by lazy { MutableLiveData(-1) }
	
	val listOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	val randomSeed: MutableLiveData<Int> by lazy { MutableLiveData(1) }
	
	
}