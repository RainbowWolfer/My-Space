package com.rainbowwolfer.myspacedemo1.ui.activities.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.repositories.UserPreferencesRepository.Companion.hasValue
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import kotlinx.coroutines.*
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)
		
//		val data = application.userPreferencesRepository.getValue()
//		data.asLiveData().observe(this) {
//			if (it.hasValue()) {
//				CoroutineScope(Dispatchers.IO).launch {
//					try {
//						val response = RetrofitInstance.api.tryLogin(it.email, it.password)
//						val user = response.body()!!
//						application.currentUser.value = user
//					} catch (ex: Exception) {
//						ex.printStackTrace()
//					}
//				}
//			}
//			CoroutineScope(Dispatchers.Main).launch {
//				delay(1000)
//				startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
//				finish()
//			}
//		}
	
	}
	
}