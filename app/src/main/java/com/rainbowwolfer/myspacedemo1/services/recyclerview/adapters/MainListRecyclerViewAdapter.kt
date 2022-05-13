package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragmentDirections
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.addPost
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.getImage
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.updateImage
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.addUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findAvatar
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUser
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.updateAvatar
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.gridview.adapters.ImagesDisplayGridViewAdapter
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainListRecyclerViewAdapter(
	private val context: Context,
) : RecyclerView.Adapter<MainListRecyclerViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: MainRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	var enableAvatarClicking: Boolean = true
	
	private var postsList = emptyList<Post>()
	private val application = MySpaceApplication.instance
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(MainRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (position == postsList.size - 1) {
//			holder.itemView.marginBottom = context.resources.getDimension(R.dimen.bottom_margin).toInt()
		}
		val data = postsList[position]
		application.postImagesPool.addPost(data)
		holder.binding.rowTextPublisherName.text = "..."
		holder.binding.rowTextPublishDateTime.text = data.publishDateTime
		holder.binding.rowTextContent.text = data.textContent
		
		holder.binding.rowGridviewImages.isVerticalScrollBarEnabled = false
		
		holder.binding.setRepostView(data.isRepost)
		holder.binding.setTags(data.tags)
		holder.binding.setMetas(data.repost, data.comment, data.downvotes, data.upvotes)
		holder.binding.loadUser(data.publisherID)
		holder.binding.loadImages(data)
		
		holder.binding.root.setOnClickListener {
			val navController = Navigation.findNavController(holder.itemView)
			navController.navigate(HomeFragmentDirections.actionItemHomeToPostDetailFragment(Post.generateDefault()))
		}
		
		holder.binding.mainLayoutRepost.setOnClickListener {
			val navController = Navigation.findNavController(holder.itemView)
			navController.navigate(HomeFragmentDirections.actionItemHomeToPostDetailFragment(Post.generateDefault()))
		}
		
		holder.binding.rowButtonMore.setOnClickListener {
			val popupMenu = PopupMenu(context, holder.binding.rowButtonMore)
			popupMenu.setOnMenuItemClickListener {
				when (it.itemId) {
					R.id.item_share -> {
						val sharedIntent = Intent().apply {
							this.action = Intent.ACTION_SEND
							this.putExtra(Intent.EXTRA_TEXT, "This is a test")
							this.type = "text/plain"
						}
						context.startActivity(sharedIntent)
					}
					R.id.item_flag -> Toast.makeText(context, "Flag", Toast.LENGTH_SHORT).show()
					R.id.item_report -> Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show()
				}
				true
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				popupMenu.setForceShowIcon(true)
			}
			popupMenu.inflate(R.menu.more_button_menu)
			popupMenu.show()
		}
		if (enableAvatarClicking) {
			holder.binding.rowImagePublisherAvatar.setOnClickListener {
				val navController = Navigation.findNavController(holder.itemView)
				navController.graph.findNode(R.id.userFragment)?.label = "User ${data.publisherID}"
				val action = HomeFragmentDirections.actionItemHomeToUserFragment3(User.getTestLogUser())
				navController.navigate(action)
			}
		}
	}
	
	private fun MainRowLayoutBinding.setRepostView(enable: Boolean) {
		arrayListOf(
			this.mainLayoutRepost,
			this.mainTextRepost,
			this.mainImageRepostAvatar,
			this.rowTextRepostInfo,
			this.rowImageRepostIcon,
		).forEach {
			(if (enable) View.VISIBLE else View.GONE).apply {
				it.visibility = this
			}
		}
	}
	
	private fun MainRowLayoutBinding.setTags(tags: String) {
		this.rowChipGroupsTags.removeAllViews()
		tags.split(',').forEach {
			if (!TextUtils.isEmpty(it)) {
				this.rowChipGroupsTags.addView(Chip(context).apply {
					text = it
				})
			}
		}
	}
	
	private fun MainRowLayoutBinding.setMetas(repost: Int, comment: Int, down: Int, up: Int) {
		this.rowRepostCount.text = "$repost"
		this.rowCommentCount.text = "$comment"
		this.rowTextScore.text = "${up - down}"
	}
	
	private fun MainRowLayoutBinding.loadUser(userID: String) {
		this.rowImagePublisherAvatar.setImageDrawable(null)
		val found = application.usersPool.findUserInfo(userID)
		if (found != null) {
			this.rowTextPublisherName.text = found.user.username
			this.rowImagePublisherAvatar.setImageBitmap(found.avatar)
			return
		}
		CoroutineScope(Dispatchers.Main).launch {
			try {
				var user = application.usersPool.findUser(userID)
				if (user == null) {
					withContext(Dispatchers.IO) {
						val response = RetrofitInstance.api.getUser(userID)
						if (response.isSuccessful) {
							user = response.body()!!
						} else {
							val go = response.getHttpResponse()
							throw ResponseException(go)
						}
					}
					application.usersPool.addUser(user!!)
				}
				this@loadUser.rowTextPublisherName.text = user!!.username
				
				var avatarBitmap = application.usersPool.findAvatar(userID)
				if (avatarBitmap == null) {
					withContext(Dispatchers.IO) {
						try {
							val response = RetrofitInstance.api.getAvatar(user!!.id)
							avatarBitmap = BitmapFactory.decodeStream(response.byteStream())
						} catch (ex: HttpException) {
							throw ResponseException(ex.response()!!.getHttpResponse())
						}
					}
					application.usersPool.updateAvatar(user!!.id, avatarBitmap)
				}
				this@loadUser.rowImagePublisherAvatar.setImageBitmap(avatarBitmap)
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
	
	private fun MainRowLayoutBinding.loadImages(post: Post) {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				val list = arrayListOf<Bitmap>()
				for (index in 0 until post.imagesCount) {
					var image = application.postImagesPool.getImage(post.id, index)
					if (image == null) {
						withContext(Dispatchers.IO) {
							val response = RetrofitInstance.api.getPostImage(post.id, index)
							val bitmap = BitmapFactory.decodeStream(response.byteStream())
							application.postImagesPool.updateImage(post.id, bitmap, index)
						}
						image = application.postImagesPool.getImage(post.id, index) ?: continue
					}
					list.add(image)
				}
				this@loadImages.rowGridviewImages.adapter = ImagesDisplayGridViewAdapter(context, post).also {
					it.list = list
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is HttpException) {
				
				} else if (ex is ResponseException) {
				
				}
			} finally {
				this@loadImages.rowGridviewImages.layoutParams.height = context.resources.getDimension(
					when (post.imagesCount) {
						in 1..3 -> R.dimen.image_preview_row_1
						in 4..6 -> R.dimen.image_preview_row_2
						in 7..9 -> R.dimen.image_preview_row_3
						else -> R.dimen.none
					}
				).toInt()
			}
		}
	}
	
	override fun getItemCount(): Int = postsList.size
	
	fun setData(newPersonList: List<Post>) {
		val diffUtil = DatabaseIdDiffUtil(postsList, newPersonList)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		postsList = newPersonList
		diffResult.dispatchUpdatesTo(this)
	}
}