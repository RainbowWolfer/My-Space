package com.rainbowwolfer.myspacedemo1.ui.activities.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.databinding.ActivityMainBinding
import com.rainbowwolfer.myspacedemo1.models.User

class MainActivity : AppCompatActivity() {
	private val binding: ActivityMainBinding by viewBinding()
	
	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var navController: NavController
	
	private lateinit var navHeaderMain: NavHeaderMain
	
	private val intentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode == Activity.RESULT_OK) {
			val v1 = result.data?.getStringExtra("key1")
			val v2 = result.data?.getStringExtra("key2")
			val v3 = result.data?.getStringExtra("key3")
			println(v1)
			println(v2)
			println(v3)
			updateNavMenu()
		}
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
		navHeaderMain = NavHeaderMain(this, viewGroup)
//		registerForActivityResult(ActivityResultContract, ActivityResultCallback)
		navHeaderMain.buttonLogin.setOnClickListener {
//			startActivityForResult(Intent(this, LoginActivity::class.java))
//			registerForActivityResult()
			intentLauncher.launch(Intent(this, LoginActivity::class.java))
		}
		
		updateNavMenu()
	}

//	override fun onCreateOptionsMenu(menu: Menu): Boolean {
//		menuInflater.inflate(R.menu.main, menu)
//		return true
//	}
	
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}
	
	override fun onBackPressed() {
		super.onBackPressed()
		binding.drawerLayout.closeDrawers()
	}
	
	private fun updateNavMenu() {
		arrayListOf(
			R.id.subnav_message,
			R.id.nav_collection,
			R.id.nav_profile,
		).forEach {
			binding.navView.menu.findItem(it).isEnabled = User.current != null
		}
	}
}