package com.rainbowwolfer.myspacedemo1.activities.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityLoginBinding
import com.rainbowwolfer.myspacedemo1.fragments.main.user.adapters.LoginViewPagerAdapter

class LoginActivity : AppCompatActivity() {
	private val binding: ActivityLoginBinding by viewBinding()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		
		val loginViewPagerAdapter = LoginViewPagerAdapter(this, supportFragmentManager, lifecycle)
		
		binding.loginViewPager.adapter = loginViewPagerAdapter
		binding.loginViewPager.isUserInputEnabled = false
		
		
		binding.materialButtonToggleGroup.check(R.id.button_login);
		binding.materialButtonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
			if (isChecked) {
				binding.loginViewPager.currentItem = when (checkedId) {
					R.id.button_login -> 0
					R.id.button_signup -> 1
					else -> 0
				}
			}
		}
	}
	
	fun getBack() {
		val data = Intent().apply {
			putExtra("key1", "value1")
			putExtra("key2", "value2")
			putExtra("key3", "value3")
			
			setResult(Activity.RESULT_OK, this)
		}
		finish()
	}
	
	override fun onNavigateUp(): Boolean {
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}