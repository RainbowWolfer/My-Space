package com.rainbowwolfer.myspacedemo1.ui.activities.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityLoginBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.LoginFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.SignUpFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.adapters.LoginViewPagerAdapter

class LoginActivity : AppCompatActivity() {
	private val binding: ActivityLoginBinding by viewBinding()
	
	companion object {
		var Instance: LoginActivity? = null
	}
	
	init {
		Instance = this
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
		
		val fragment = binding.loginViewPager.findFragmentAtPosition(supportFragmentManager, 0)
		if (fragment is LoginFragment) {
			fragment.fill(email, password)
		}
	}
	
	fun ViewPager2.findFragmentAtPosition(
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
}