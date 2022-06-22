package com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.records.RepostRecord
import com.rainbowwolfer.myspacedemo1.models.records.ScoreRecord

class PostDetailViewModel : ViewModel() {
	val post: MutableLiveData<Post> by lazy { MutableLiveData() }
	
	val comments: MutableLiveData<List<Comment>> by lazy { MutableLiveData(emptyList()) }
	val commentsOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	
	val repostRecords: MutableLiveData<List<RepostRecord>> by lazy { MutableLiveData(emptyList()) }
	val repostRecordsOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	
	val scoreRecords: MutableLiveData<List<ScoreRecord>> by lazy { MutableLiveData(emptyList()) }
	val scoreRecordsOffset: MutableLiveData<Int> by lazy { MutableLiveData(0) }
	
}