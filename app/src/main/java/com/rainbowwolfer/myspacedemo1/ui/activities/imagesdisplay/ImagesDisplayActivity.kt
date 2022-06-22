package com.rainbowwolfer.myspacedemo1.ui.activities.imagesdisplay

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.viewbinding.library.activity.viewBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.ActivityImagesDisplayBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findPostInfo
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.findRelativePosts
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.getImage
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateRepostButton
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.postDetail.PostDetailFragment.Companion.updateVoteButtons
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay.adapters.ImagesPoolViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.setAutoClearEditTextFocus
import com.rainbowwolfer.myspacedemo1.util.ImageUtils
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils
import com.rainbowwolfer.myspacedemo1.util.SheetDialogUtil

class ImagesDisplayActivity : AppCompatActivity() {
	companion object {
		const val ARG_POST_ID = "post_id"
		const val ARG_CURRENT = "current"
	}
	
	private val binding: ActivityImagesDisplayBinding by viewBinding()
	private val application = MySpaceApplication.instance
	
	private lateinit var postID: String
	private lateinit var post: Post
	private lateinit var adapter: ImagesPoolViewPagerAdapter
	private var current: Int = -1
	
	override fun attachBaseContext(newBase: Context?) {
		super.attachBaseContext(LocaleUtils.attachBaseContext(newBase))
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = ""
		
		postID = intent.getStringExtra(ARG_POST_ID) ?: ""
		post = application.postsPool.findPostInfo(postID)?.post ?: return
		current = intent.getIntExtra(ARG_CURRENT, -1)
		
		setMetas(postID)
		updateVoteButtons(binding.imagesDisplayButtonUp, binding.imagesDisplayButtonDown, post.readVoted())
		
		with(binding.imagesDisplayViewPager2) {
			this.adapter = ImagesPoolViewPagerAdapter(supportFragmentManager, lifecycle, post.readID(), post.readImagesCount()).also {
				adapter = it
			}
			//both have to be here
			this.currentItem = current
			this.setCurrentItem(currentItem, false)
		}
		
		binding.imagesDisplayButtonUp.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			if (post.readVoted() != Post.VOTE_UP) {
				application.votePost(post.readID(), true)
				if (post.readVoted() == Post.VOTE_DOWN) {
					post.updateDownvotes(-1)
				}
				post.updateVoted(Post.VOTE_UP)
				post.updateUpvotes(1)
			} else {
				application.votePost(post.readID(), null)
				post.updateVoted(Post.VOTE_NONE)
				post.updateUpvotes(-1)
			}
			
			updateVoteButtons(binding.imagesDisplayButtonUp, binding.imagesDisplayButtonDown, post.readVoted())
			setMetas(postID)
			
			with(application.postsPool.findRelativePosts(post.readID())) {
				this.forEach {
					if (it.isRepost) {
						it.originUpvotes = post.upvotes
						it.originDownvotes = post.downvotes
						it.originVoted = post.voted
					} else {
						it.upvotes = post.upvotes
						it.downvotes = post.downvotes
						it.voted = post.voted
					}
				}
			}
		}
		
		binding.imagesDisplayButtonDown.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			if (post.readVoted() != Post.VOTE_DOWN) {
				application.votePost(post.id, false)
				if (post.readVoted() == Post.VOTE_UP) {
					post.updateUpvotes(-1)
				}
				post.updateVoted(Post.VOTE_DOWN)
				post.updateDownvotes(1)
			} else {
				application.votePost(post.id, null)
				post.updateVoted(Post.VOTE_NONE)
				post.updateDownvotes(-1)
			}
			
			updateVoteButtons(binding.imagesDisplayButtonUp, binding.imagesDisplayButtonDown, post.readVoted())
			setMetas(postID)
			
			with(application.postsPool.findRelativePosts(post.readID())) {
				this.forEach {
					if (it.isRepost) {
						it.originUpvotes = post.upvotes
						it.originDownvotes = post.downvotes
						it.originVoted = post.voted
					} else {
						it.upvotes = post.upvotes
						it.downvotes = post.downvotes
						it.voted = post.voted
					}
				}
			}
			
		}
		
		binding.imagesDisplayButtonComment.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			SheetDialogUtil.showCommentDialog(this, post.readID()) {
				post.updateComments(1)
				setMetas(post.readID())
				
				with(application.postsPool.findRelativePosts(post.readID())) {
					this.forEach {
						if (it.isRepost) {
							it.originComments = post.comments
						} else {
							it.comments = post.comments
						}
					}
				}
			}
		}
		
		binding.imagesDisplayButtonRepost.buttonAction {
			val post = application.postsPool.findPostInfo(postID)?.post ?: return@buttonAction
			SheetDialogUtil.showRepostDialog(this, post.readID()) {
				post.updateReposts(1)
				setMetas(post.readID())
				
				with(application.postsPool.findRelativePosts(post.readID())) {
					this.forEach {
						if (it.isRepost) {
							it.originReposts = post.reposts
						} else {
							it.reposts = post.reposts
						}
					}
				}
			}
		}
	}
	
	private fun setMetas(postID: String) {
		val post = application.postsPool.findPostInfo(postID)?.post ?: return
		
		binding.imagesDisplayButtonRepost.text = "${post.readReposts()}"
		binding.imagesDisplayButtonComment.text = "${post.readComments()}"
		binding.imagesDisplayTextViewScore.text = "${post.readUpvotes() - post.readDownvotes()}"
		
		updateRepostButton(binding.imagesDisplayButtonRepost, post.hasReposted)
	}
	
	private fun View.buttonAction(action: () -> Unit) {
		this.setOnClickListener {
			if (application.hasLoggedIn()) {
				action.invoke()
			} else {
				HomeFragment.popupNotLoggedInHint(this)
			}
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
				try {
					val position = binding.imagesDisplayViewPager2.currentItem
					val bitmap = MySpaceApplication.instance.postsPool.getImage(postID, position)
					Toast.makeText(this, "$position ${bitmap?.bitmap}", Toast.LENGTH_SHORT).show()
					ImageUtils.saveBitmap(bitmap!!.bitmap.value!!)
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
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