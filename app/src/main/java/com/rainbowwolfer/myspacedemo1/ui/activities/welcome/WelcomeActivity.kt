package com.rainbowwolfer.myspacedemo1.ui.activities.welcome

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils

class WelcomeActivity : AppCompatActivity() {
	
	override fun attachBaseContext(newBase: Context?) {
		super.attachBaseContext(LocaleUtils.attachBaseContext(newBase))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)
	}
	
}