package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo

class UserFragmentViewModel : ViewModel() {
	val userInfo: MutableLiveData<UserInfo?> by lazy { MutableLiveData(null) }
	val postsAndFollowersCount: MutableLiveData<Pair<Int, Int>> by lazy { MutableLiveData(Pair(-1, -1)) }
	
	val userPosts: MutableLiveData<List<Post>> by lazy { MutableLiveData(emptyList()) }
	val userPostsOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	
	val followers: MutableLiveData<List<User>> by lazy { MutableLiveData(emptyList()) }
	val followersOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
}