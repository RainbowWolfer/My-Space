package com.rainbowwolfer.myspacedemo1.activities.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.activities.main.MainActivity

class WelcomeActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)
		
		Handler(Looper.getMainLooper()).postDelayed({
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}, 2000)
	}
}