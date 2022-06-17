package com.rainbowwolfer.myspacedemo1.ui.activities.imagesdisplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.viewbinding.library.activity.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityImagesDisplayBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay.adapters.ImagesPoolViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.setAutoClearEditTextFocus

class ImagesDisplayActivity : AppCompatActivity() {
	companion object {
		const val ARG_POST = "post"
		const val ARG_CURRENT = "current"
	}
	
	private val binding: ActivityImagesDisplayBinding by viewBinding()
	
	private var post: Post? = null
	private var current: Int = -1
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = ""
		
		post = intent.getParcelableExtra(ARG_POST)
		current = intent.getIntExtra(ARG_CURRENT, -1)
		
		binding.imagesDisplayButtonRepost.text = "${post?.reposts ?: 0}"
		binding.imagesDisplayButtonComment.text = "${post?.comments ?: 0}"
		binding.imagesDisplayTextViewScore.text = "${post?.score ?: 0}"
		
		val adapter = ImagesPoolViewPagerAdapter(supportFragmentManager, lifecycle, post!!.id, post!!.imagesCount)
		with(binding.imagesDisplayViewPager2) {
			this.adapter = adapter
			//both have to be here
			this.currentItem = current
			this.setCurrentItem(currentItem, false)
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
	
	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		this.setAutoClearEditTextFocus(event)
		return super.dispatchTouchEvent(event)
	}
}