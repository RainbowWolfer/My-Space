package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.enums.PostsLimit

class HomeFragmentViewModel : ViewModel() {
	val postsLimit: MutableLiveData<PostsLimit> by lazy { MutableLiveData(PostsLimit.All) }
	val posts: MutableLiveData<List<Post>> by lazy { MutableLiveData(emptyList()) }
	
	
}