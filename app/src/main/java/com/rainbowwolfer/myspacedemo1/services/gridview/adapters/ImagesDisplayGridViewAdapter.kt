package com.rainbowwolfer.myspacedemo1.services.gridview.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.LayoutImageDisplayGridviewItemBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.ui.activities.imagesdisplay.ImagesDisplayActivity

class ImagesDisplayGridViewAdapter(
	context: Context,
	private val post: Post,
) : ArrayAdapter<ImagesDisplayGridViewAdapter.ItemViewHolder>(
	context, R.layout.layout_image_display_gridview_item
) {
	class ItemViewHolder(val binding: LayoutImageDisplayGridviewItemBinding)
	
	var list: ArrayList<Bitmap> = arrayListOf()
	
	override fun getCount(): Int = list.size
	
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		var view = convertView
		val holder: ItemViewHolder
		if (view == null) {
			val binding = LayoutImageDisplayGridviewItemBinding.inflate(LayoutInflater.from(context))
			view = binding.root
			holder = ItemViewHolder(binding)
			view.tag = holder
		} else {
			holder = view.tag as ItemViewHolder
		}
		holder.binding.gridViewItemImageDisplay.setImageBitmap(list[position])
		holder.binding.gridViewItemImageDisplay.setOnClickListener {
			println("HELLO")
			val intent = Intent(context, ImagesDisplayActivity::class.java).apply {
				putExtra(ImagesDisplayActivity.ARG_POST_ID, post.id)
				putExtra(ImagesDisplayActivity.ARG_SIZE, post.imagesCount)
				putExtra(ImagesDisplayActivity.ARG_CURRENT, position)
			}
			context.startActivity(intent)
		}
		return view
	}
	
	override fun areAllItemsEnabled(): Boolean {
		return false
	}
	
	override fun isEnabled(position: Int): Boolean {
		return false
	}
}