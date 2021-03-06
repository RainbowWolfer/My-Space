package com.rainbowwolfer.myspacedemo1.ui.activities.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityLoginBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.login.LoginFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.login.adapters.LoginViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.setAutoClearEditTextFocus
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils

class LoginActivity : AppCompatActivity() {
	private val binding: ActivityLoginBinding by viewBinding()
	
	companion object {
		var Instance: LoginActivity? = null
	}
	
	init {
		Instance = this
	}
	
	override fun attachBaseContext(newBase: Context?) {
		super.attachBaseContext(LocaleUtils.attachBaseContext(newBase))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		
		val loginViewPagerAdapter = LoginViewPagerAdapter(this, supportFragmentManager, lifecycle)
		
		binding.loginViewPager.adapter = loginViewPagerAdapter
		binding.loginViewPager.isUserInputEnabled = false
		
		binding.materialButtonToggleGroup.check(R.id.button_login)
		binding.materialButtonToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
			if (isChecked) {
				binding.loginViewPager.currentItem = when (checkedId) {
					R.id.button_login -> 0
					R.id.button_signup -> 1
					else -> 0
				}
			}
		}
	}
	
	fun toLoginFragment(email: String? = null, password: String? = null) {
		binding.materialButtonToggleGroup.check(R.id.button_login)
		binding.loginViewPager.currentItem = 0
		
		val fragment = findFragmentAtPosition(supportFragmentManager, 0)
		if (fragment is LoginFragment) {
			fragment.fill(email, password)
		}
	}
	
	private fun findFragmentAtPosition(
		fragmentManager: FragmentManager,
		position: Int
	): Fragment? {
		return fragmentManager.findFragmentByTag("f$position")
	}
	
	fun getBack(user: User) {
		Intent().apply {
			putExtra("user", user)
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
	
	
	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		this.setAutoClearEditTextFocus(event)
		return super.dispatchTouchEvent(event)
	}
}