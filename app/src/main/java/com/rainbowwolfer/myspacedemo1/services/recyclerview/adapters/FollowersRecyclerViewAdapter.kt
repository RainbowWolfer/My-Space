package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowFollowersEmptyLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.RowFollowersLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.models.api.NewUserFollow
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.loadAvatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowersRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val targetUserID: String = "",
) : RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder>() {
	companion object {
		const val TYPE_ROW = 0
		const val TYPE_EMPTY = 1
		const val TYPE_IGNORE = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowViewHolder(val binding: RowFollowersLayoutBinding) : ViewHolder(binding.root)
	class EmptyViewHolder(val binding: RowFollowersEmptyLayoutBinding) : ViewHolder(binding.root)
	
	private var list = emptyList<User>()
	private val application = MySpaceApplication.instance
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPE_EMPTY -> {
				EmptyViewHolder(RowFollowersEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			TYPE_ROW -> {
				RowViewHolder(RowFollowersLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			else -> {
				ViewHolder(View(context))
			}
		}
	}
	
	override fun getItemViewType(position: Int): Int {
		return if (list.isEmpty()) {
			TYPE_EMPTY
		} else {
			if (position >= list.size) {
				TYPE_IGNORE
			} else {
				TYPE_ROW
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowViewHolder) {
			val data = list[position]
			holder.binding.rowFollowersTextUsername.text = data.username
			holder.updateFollowButtonIcon(data)
			
			if (data.id != targetUserID) {
				holder.binding.rowFollowersImageAvatar.setOnClickListener {
					val navController = Navigation.findNavController(holder.itemView)
					navController.navigate(R.id.item_user_profile, Bundle().apply {
						putString("user_id", data.id)
					}, EasyFunctions.defaultTransitionNavOption())
				}
			}
			if (data.id != application.getCurrentID()) {
				holder.binding.rowFollowersButtonFollow.setOnClickListener {
					if (!application.hasLoggedIn()) {
						return@setOnClickListener
					}
					lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
						try {
							holder.binding.rowFollowersButtonFollow.isEnabled = false
							holder.binding.rowFollowersButtonFollow.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_more_horiz_24))
							withContext(Dispatchers.IO) {
								RetrofitInstance.api.postUserFollow(
									NewUserFollow(
										email = application.getCurrentEmail(),
										password = application.getCurrentPassword(),
										targetID = data.id,
										cancel = data.isFollowing,
									)
								)
							}
							data.isFollowing = !data.isFollowing
						} catch (ex: Exception) {
							ex.printStackTrace()
							if (ex is ResponseException) {
								ex.printResponseException()
							}
						} finally {
							try {
								holder.binding.rowFollowersButtonFollow.isEnabled = true
								holder.updateFollowButtonIcon(data)
							} catch (ex: Exception) {
							}
						}
					}
				}
			} else {
				holder.binding.rowFollowersButtonFollow.visibility = View.GONE
			}
			
			lifecycleOwner.loadAvatar(data.id) {
				holder.binding.rowFollowersImageAvatar.setImageBitmap(it)
			}
		}
	}
	
	private fun RowViewHolder.updateFollowButtonIcon(user: User) {
		binding.rowFollowersButtonFollow.setImageDrawable(
			AppCompatResources.getDrawable(
				context,
				if (user.isFollowing) {
					R.drawable.ic_baseline_favorite_24
				} else {
					R.drawable.ic_baseline_favorite_border_24
				}
			)
		)
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	fun setData(new: List<User>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		list = new
		DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
	}
}