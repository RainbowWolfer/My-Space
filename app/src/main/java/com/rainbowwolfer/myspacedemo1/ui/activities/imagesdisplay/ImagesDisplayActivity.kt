package com.rainbowwolfer.myspacedemo1.ui.activities.imagesdisplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityImagesDisplayBinding
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay.adapters.ImagesPoolViewPagerAdapter

class ImagesDisplayActivity : AppCompatActivity() {
	private val binding: ActivityImagesDisplayBinding by viewBinding()
	
	private var postID: String? = null
	private var size: Int = -1
	private var current: Int = -1
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = ""
		
		postID = intent.getStringExtra(ARG_POST_ID)
		size = intent.getIntExtra(ARG_SIZE, -1)
		current = intent.getIntExtra(ARG_CURRENT, -1)
		
		val adapter = ImagesPoolViewPagerAdapter(supportFragmentManager, lifecycle, postID!!, size)
		with(binding.imagesDisplayViewPager2) {
			this.adapter = adapter
			this.currentItem = current
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.display_image_menu, menu)
		return true
	}
	
	override fun onNavigateUp(): Boolean {
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.item_download -> {
				
				true
			}
			android.R.id.home -> {
				onBackPressed()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
	
	companion object {
		const val ARG_POST_ID = "post_id"
		const val ARG_SIZE = "size"
		const val ARG_CURRENT = "current"
	}
}