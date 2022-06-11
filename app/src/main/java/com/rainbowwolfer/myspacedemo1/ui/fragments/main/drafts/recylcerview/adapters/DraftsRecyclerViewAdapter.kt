package com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts.recylcerview.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.RowDraftLayoutBinding
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.diff.DatabaseIdDiffUtil
import com.rainbowwolfer.myspacedemo1.ui.activities.post.PostActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DraftsRecyclerViewAdapter(
	private val context: Context,
	private val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<DraftsRecyclerViewAdapter.ViewHolder>() {
	class ViewHolder(val binding: RowDraftLayoutBinding) : RecyclerView.ViewHolder(binding.root)
	
	private val application = MySpaceApplication.instance
	private var list = emptyList<Draft>()
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(RowDraftLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = list[position]
		holder.binding.draftRowButtonMore.setOnClickListener {
			val popupMenu = PopupMenu(context, it)
			popupMenu.setOnMenuItemClickListener { item ->
				when (item.itemId) {
					R.id.item_edit -> {
						goPostActivity(data)
					}
					R.id.item_delete -> {
						AlertDialog.Builder(context).apply {
							setTitle("Delete")
							setMessage("Are you sure to delete this draft?")
							setNegativeButton("No", null)
							setPositiveButton("Yes") { _, _ ->
								lifecycleCoroutineScope.launch(Dispatchers.IO) {
									application.roomRepository.delete(data)
								}
							}
							create()
							show()
						}
					}
				}
				true
			}
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				popupMenu.setForceShowIcon(true)
			}
			
			popupMenu.inflate(R.menu.draft_more_menu)
			popupMenu.show()
		}
		
		holder.binding.root.setOnClickListener {
			goPostActivity(data)
		}
		
		holder.binding.draftRowTextTime.text = data.addedDateTime
		holder.binding.draftRowTextContent.text = data.textContent
	}
	
	private fun goPostActivity(draft: Draft) {
		context.startActivity(Intent(context, PostActivity::class.java).apply {
			putExtra(PostActivity.ARGS_DRAFT, draft)
		})
	}
	
	override fun getItemCount(): Int = list.size
	
	fun setData(new: List<Draft>) {
		val diffUtil = DatabaseIdDiffUtil(list, new)
		val diffResult = DiffUtil.calculateDiff(diffUtil)
		list = new
		diffResult.dispatchUpdatesTo(this)
	}
}