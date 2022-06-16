package com.rainbowwolfer.myspacedemo1.ui.fragments.main.collections.adapters.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowCollectionEmptyLayoutBinding
import com.rainbowwolfer.myspacedemo1.databinding.RowCollectionPostLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.UserCollection
import com.rainbowwolfer.myspacedemo1.models.api.RemoveCollection
import com.rainbowwolfer.myspacedemo1.models.enums.CollectionType
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.services.recyclerview.interfaces.IRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.collections.CollectionFragmentDirections
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionsRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<CollectionsRecyclerViewAdapter.ViewHolder>(), IRecyclerViewAdapter<UserCollection> {
	companion object {
		const val TYPE_ROW = 0
		const val TYPE_EMPTY = 1
		const val TYPE_IGNORE = 2
	}
	
	open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
	class RowPostViewHolder(val binding: RowCollectionPostLayoutBinding) : ViewHolder(binding.root)
	class EmptyViewHolder(val binding: RowCollectionEmptyLayoutBinding) : ViewHolder(binding.root)
	
	override var list: List<UserCollection> = emptyList()
	private val application = MySpaceApplication.instance
	
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
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			TYPE_EMPTY -> {
				EmptyViewHolder(RowCollectionEmptyLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			TYPE_ROW -> {
				RowPostViewHolder(RowCollectionPostLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
			}
			else -> {
				ViewHolder(View(context))
			}
		}
	}
	
	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is RowPostViewHolder) {
			val data = list[position]
			holder.binding.rowCollectionPostTextDate.text = data.time
			holder.binding.rowCollectionPostTextUsername.text = "@${data.publisherUsername}"
			holder.binding.rowCollectionPostIconRepost.visibility = if (data.isRepost) View.VISIBLE else View.GONE
			with(if (data.imagesCount != 0) View.VISIBLE else View.GONE) {
				holder.binding.rowCollectionPostIconImage.visibility = this
				holder.binding.rowCollectionPostTextImageCount.visibility = this
			}
			holder.binding.rowCollectionPostTextContent.text = data.textContent
			
			holder.binding.rowCollectionPostButtonMore.setOnClickListener {
				val popupMenu = PopupMenu(context, holder.binding.rowCollectionPostButtonMore)
				popupMenu.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.item_delete -> {
							if (application.hasLoggedIn()) {
								lifecycleCoroutineScope.launch(Dispatchers.IO) {
									try {
										RetrofitInstance.api.deleteCollection(
											RemoveCollection(
												targetID = data.targetID,
												email = application.currentUser.value?.email ?: "",
												password = application.currentUser.value?.password ?: "",
											)
										)
										withContext(Dispatchers.Main) {
											Toast.makeText(context, "Succesffully Removed From Collections", Toast.LENGTH_SHORT).show()
											setData(list.filter { item ->
												item.targetID != data.targetID
											})
										}
									} catch (ex: Exception) {
										ex.printStackTrace()
									}
								}
							}
						}
					}
					true
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					popupMenu.setForceShowIcon(true)
				}
				popupMenu.inflate(R.menu.collection_item_more_menu)
				popupMenu.show()
			}
			
			holder.binding.root.setOnClickListener {
				if (data.type == CollectionType.Post) {
					val navController = Navigation.findNavController(holder.itemView)
					navController.navigate(
						CollectionFragmentDirections.actionNavCollectionToPostDetailFragment2(data.targetID)
					)
				}
			}
		}
	}
	
	private fun navigateToUserProfile(view: View, id: String) {
		val navController = Navigation.findNavController(view)
		navController.graph.findNode(R.id.userFragment)?.label = "User $id"
		val action = HomeFragmentDirections.actionItemHomeToUserFragment(id)
		navController.navigate(action)
	}
	
	override fun getItemCount(): Int = list.size + 1
	
	override fun setData(new: List<UserCollection>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		list = new
		DiffUtil.calculateDiff(diffUtil).dispatchUpdatesTo(this)
	}
}