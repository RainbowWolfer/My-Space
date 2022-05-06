package com.rainbowwolfer.myspacedemo1.services

import android.content.Context
import android.graphics.BitmapFactory
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.models.User
import kotlinx.coroutines.delay

object App {
	//TODO: LEARN VIEW MODEL!!!!!
	suspend fun initialize(context: Context) {
		User.avatar.value = BitmapFactory.decodeResource(context.resources, R.drawable.default_avatar)
		delay(100)
	}
}