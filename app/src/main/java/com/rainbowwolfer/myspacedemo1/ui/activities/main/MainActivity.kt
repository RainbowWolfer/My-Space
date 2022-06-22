package com.rainbowwolfer.myspacedemo1.ui.activities.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.activity.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
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
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.datastore.repositories.UserPreferencesRepository.Companion.hasUserValue
import com.rainbowwolfer.myspacedemo1.ui.activities.login.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.fragments.FragmentCustomBackPressed
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.setAutoClearEditTextFocus
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils
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
	private val viewModel: MainActivityViewModel by viewModels()
	private val application = MySpaceApplication.instance
	
	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var navController: NavController
	
	private lateinit var navViewHeaderBinding: NavHeaderMainBinding
	
	val drawerLayout get() = binding.drawerLayout
	
	val loginIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
		if (result.resultCode != Activity.RESULT_OK) {
			return@registerForActivityResult
		}
		val user = result.data?.getParcelableExtra<User>("user")
		application.currentUser.value = user
		if (user == null) {
			return@registerForActivityResult
		}
		application.updateAvatar()
		lifecycleScope.launch(Dispatchers.Main) {
			application.userPreferencesRepository.updateUser(user.email, user.password)
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
	
	override fun onResume() {
		super.onResume()
		if (application.hasLoggedIn()) {
			return
		}
		val data = application.userPreferencesRepository.getValue()
		data.asLiveData().observe(this) {
			if (it.hasUserValue()) {
				lifecycleScope.launch(Dispatchers.IO) {
					try {
						val response = RetrofitInstance.api.tryLogin(it.email, it.password)
						val user = response.body()!!
						withContext(Dispatchers.Main) {
							application.currentUser.value = user
						}
					} catch (ex: Exception) {
						ex.printStackTrace()
					}
				}
			}
		}
	}
	
	override fun attachBaseContext(newBase: Context?) {
		super.attachBaseContext(LocaleUtils.attachBaseContext(newBase))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		setSupportActionBar(binding.appBarMain.toolbar)
		
		application.currentUser.observe(this) {
			application.updateAvatar()
			updateNav()
		}
		
		application.currentAvatar.observe(this) {
			navViewHeaderBinding.headerImageAvatar.setImageBitmap(
				it ?: BitmapFactory.decodeResource(resources, R.drawable.default_avatar)
			)
		}
		
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
		navController = navHostFragment.navController
		
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.item_home,
				R.id.nav_message,
				R.id.nav_settings,
				R.id.nav_about,
				R.id.nav_collection,
				R.id.nav_profile,
				R.id.nav_drafts,
			), binding.drawerLayout
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		binding.navView.setupWithNavController(navController)
		
		binding.navView.setCheckedItem(R.id.item_home)
		
		val viewGroup: ViewGroup = binding.navView.getHeaderView(0) as ViewGroup
		navViewHeaderBinding = NavHeaderMainBinding.bind(viewGroup)
		
		navViewHeaderBinding.headerButtonLogin.setOnClickListener {
			loginIntentLauncher.launch(Intent(this, LoginActivity::class.java))
		}
		
		navViewHeaderBinding.headerButtonSignOut.setOnClickListener {
			AlertDialog.Builder(this).apply {
				setTitle(getString(R.string.confirm))
				setMessage(getString(R.string.are_you_sure_to_sign_out))
				setNegativeButton(getString(R.string.no), null)
				setPositiveButton(getString(R.string.yes)) { _, _ ->
					binding.navView.menu.findItem(R.id.item_home).isChecked = true
					navController.popBackStack(R.id.item_home, true)
					navController.navigate(R.id.item_home)
					application.clearCurrent()
					lifecycleScope.launch(Dispatchers.Main) {
						application.userPreferencesRepository.clearUser()
					}
				}
				show()
			}
		}
	}
	
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}
	
	override fun onBackPressed() {
		binding.drawerLayout.closeDrawers()
		val fragment = this.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as? NavHostFragment
		val currentFragment = fragment?.childFragmentManager?.fragments?.get(0) as? FragmentCustomBackPressed
		if (currentFragment != null) {
			currentFragment.onBackPressed().takeIf {
				!it
			}?.let {
				super.onBackPressed()
			}
		} else {
			super.onBackPressed()
		}
	}
	
	private fun updateNav() {
		arrayListOf(
			R.id.nav_message,
			R.id.nav_collection,
			R.id.nav_profile,
			R.id.nav_drafts,
		).forEach {
			binding.navView.menu.findItem(it).apply {
				isEnabled = application.hasLoggedIn()
				isChecked = false
			}
		}
		val loggedIn = application.hasLoggedIn()
		navViewHeaderBinding.headerTextTitle.text = if (loggedIn) application.currentUser.value!!.username else getString(R.string.welcome)
		navViewHeaderBinding.headerButtonLogin.visibility = if (loggedIn) View.GONE else View.VISIBLE
		navViewHeaderBinding.headerButtonSignOut.visibility = if (loggedIn) View.VISIBLE else View.GONE
	}
	
	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		this.setAutoClearEditTextFocus(event)
		return super.dispatchTouchEvent(event)
	}
	
	
	fun switchAppLanguage() {
		LocaleUtils.notifyLanguageChanged(this)
		val intent = Intent(this, MainActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(intent)
	}
}