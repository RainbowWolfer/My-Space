package com.rainbowwolfer.myspacedemo1.ui.activities.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.activity.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityMainBinding
import com.rainbowwolfer.myspacedemo1.databinding.NavHeaderMainBinding
import com.rainbowwolfer.myspacedemo1.models.PostResult
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
	companion object {
		var Instance: MainActivity? = null
	}
	
	init {
		Instance = this
	}
	
	private val binding: ActivityMainBinding by viewBinding()
	
	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var navController: NavController
	
	private lateinit var navViewHeaderBinding: NavHeaderMainBinding
	
	val loginIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode != Activity.RESULT_OK) {
			return@registerForActivityResult
		}
		val user = result.data?.getParcelableExtra<User>("user")
		User.current = user
		updateNav()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				if (user == null) {
					return@launch
				}
				val response = RetrofitInstance.api.getAvatar(user.id)
				
				withContext(Dispatchers.Main) {
					User.avatar.value = BitmapFactory.decodeStream(response.byteStream())
					navViewHeaderBinding.headerImageAvatar.setImageBitmap(User.avatar.value)
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
		
	}
	
	val postIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode != Activity.RESULT_OK) {
			return@registerForActivityResult
		}
		val post = result.data?.getParcelableExtra<PostResult>("post") ?: return@registerForActivityResult
		if (result.data?.getBooleanExtra("send", false) == true) {
			println("send")
		} else {
			println("draft")
		}
		println(post)
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		setSupportActionBar(binding.appBarMain.toolbar)
		
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
		navController = navHostFragment.navController
		
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.item_home,
				R.id.nav_message,
				R.id.nav_settings,
				R.id.nav_about,
				R.id.nav_collection,
				R.id.nav_profile
			), binding.drawerLayout
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		binding.navView.setupWithNavController(navController)
		
		binding.navView.setCheckedItem(R.id.subnav_home)
		
		val viewGroup: ViewGroup = binding.navView.getHeaderView(0) as ViewGroup
		navViewHeaderBinding = NavHeaderMainBinding.bind(viewGroup)
		
		navViewHeaderBinding.headerButtonLogin.setOnClickListener {
			loginIntentLauncher.launch(Intent(this, LoginActivity::class.java))
		}
		
		navViewHeaderBinding.headerButtonSignOut.setOnClickListener {
			AlertDialog.Builder(this).apply {
				setTitle("Confirm")
				setMessage("Are you sure to sign out?")
				setNegativeButton("No", null)
				setPositiveButton("Yes") { _, _ ->
					User.current = null
					User.avatar.value = null
					updateNav()
//					binding.drawerLayout.closeDrawers()
				}
				show()
			}
		}
		
		updateNav()
		
		User.avatar.observe(this) {
			navViewHeaderBinding.headerImageAvatar.setImageBitmap(
				if (User.avatar.value == null) {
					BitmapFactory.decodeResource(resources, R.drawable.default_avatar)
				} else {
					User.avatar.value
				}
			)
		}
		
	}
	
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}
	
	override fun onBackPressed() {
		super.onBackPressed()
		binding.drawerLayout.closeDrawers()
	}
	
	private fun updateNav() {
		arrayListOf(
			R.id.subnav_message,
			R.id.nav_collection,
			R.id.nav_profile,
		).forEach {
			binding.navView.menu.findItem(it).isEnabled = User.isLoggedIn()
		}
		navViewHeaderBinding.headerTextTitle.text = if (User.isLoggedIn()) User.current!!.username else "Welcome"
		navViewHeaderBinding.headerButtonLogin.visibility = if (User.isLoggedIn()) View.GONE else View.VISIBLE
		navViewHeaderBinding.headerButtonSignOut.visibility = if (User.isLoggedIn()) View.VISIBLE else View.GONE
		//update avatar
	}
}