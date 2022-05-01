package com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.MainRowLayoutBinding
import com.rainbowwolfer.myspacedemo1.fragments.main.home.HomeFragmentDirections
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions

class MainListRecyclerViewAdapter(
	private val context: Context,
) : RecyclerView.Adapter<MainListRecyclerViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: MainRowLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	var enableAvatarClicking: Boolean = true
	
	private var postsList = emptyList<Post>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(MainRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = postsList[position]
		holder.binding.rowTextPublisherName.text = data.publisher.username
		holder.binding.rowTextPublishDateTime.text = EasyFunctions.formatDateTime(data.publishDateTime)
		holder.binding.rowTextContent.text = data.content
		holder.binding.rowButtonMore.setOnClickListener {
			val popupMenu = PopupMenu(context, holder.binding.rowButtonMore)
			popupMenu.setOnMenuItemClickListener {
				when (it.itemId) {
					R.id.item_share -> {
						val sharedIntent = Intent().apply {
							this.action = Intent.ACTION_SEND
							this.putExtra(Intent.EXTRA_TEXT, "")
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
				navController.graph.findNode(R.id.userFragment)?.label = "User ${data.publisher.username}"
				val action = HomeFragmentDirections.actionItemHomeToUserFragment3(data.publisher)
				navController.navigate(action)
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