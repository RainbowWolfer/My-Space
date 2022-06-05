package com.rainbowwolfer.myspacedemo1.ui.activities.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rainbowwolfer.myspacedemo1.R

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