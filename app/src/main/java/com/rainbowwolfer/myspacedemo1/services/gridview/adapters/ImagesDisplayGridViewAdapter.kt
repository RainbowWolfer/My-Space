package com.rainbowwolfer.myspacedemo1.services.gridview.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.LayoutImageDisplayGridviewItemBinding
import com.rainbowwolfer.myspacedemo1.models.Post
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.getImage
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.updateImage
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.ui.activities.imagesdisplay.ImagesDisplayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class ImagesDisplayGridViewAdapter(
	context: Context,
	private val post: Post,
) : ArrayAdapter<ImagesDisplayGridViewAdapter.ItemViewHolder>(
	context, R.layout.layout_image_display_gridview_item
) {
	companion object {
		@JvmStatic
		fun GridView.presetGridViewHeight(count: Int) {
			this.layoutParams.height = context.resources.getDimension(
				when (count) {
					in 1..3 -> R.dimen.image_preview_row_1
					in 4..6 -> R.dimen.image_preview_row_2
					in 7..9 -> R.dimen.image_preview_row_3
					else -> R.dimen.none
				}
			).toInt()
		}
		
		@JvmStatic
		fun getShuffledColorLists(context: Context, count: Int): MutableList<Pair<Bitmap?, Int?>> {
			val colors = arrayListOf(
				context.getColor(R.color.color1),
				context.getColor(R.color.color2),
				context.getColor(R.color.color3),
				context.getColor(R.color.color4),
				context.getColor(R.color.color5),
				context.getColor(R.color.color6),
				context.getColor(R.color.color7),
				context.getColor(R.color.color8),
				context.getColor(R.color.color9),
			).shuffled().map {
				Pair<Bitmap?, Int?>(null, it)
			}.toMutableList()
			
			for (index in colors.indices) {
				if (index >= count) {
					colors[index] = Pair(null, context.getColor(R.color.transparent))
				}
			}
			return colors
		}
		
		@JvmStatic
		fun loadImages(gridView: GridView, post: Post) {
			val application = MySpaceApplication.instance
			CoroutineScope(Dispatchers.Main).launch {
				val adapter = gridView.adapter as ImagesDisplayGridViewAdapter
				try {
					val list = adapter.list.toMutableList()
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
						list[index] = Pair(image, null)
						adapter.setData(list)
					}
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
	}
	
	class ItemViewHolder(val binding: LayoutImageDisplayGridviewItemBinding)
	
	var list: List<Pair<Bitmap?, Int?>> = emptyList()
	
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
		if (list[position].first != null) {
			holder.binding.gridViewItemImageDisplay.setImageBitmap(list[position].first)
		} else if (list[position].second != null) {
			val color = ColorDrawable(list[position].second!!)
			holder.binding.gridViewItemImageDisplay.setImageDrawable(color)
			println("$position : ${color.alpha}")
			if (color.alpha < 200) {
				holder.binding.gridViewItemImageDisplay.visibility = View.INVISIBLE
			}
			return view
		} else {
			return view
		}
		holder.binding.gridViewItemImageDisplay.setOnClickListener {
			context.startActivity(Intent(context, ImagesDisplayActivity::class.java).apply {
				putExtra(ImagesDisplayActivity.ARG_POST, post)
				putExtra(ImagesDisplayActivity.ARG_CURRENT, position)
			})
		}
		return view
	}
	
	override fun areAllItemsEnabled(): Boolean {
		return false
	}
	
	override fun isEnabled(position: Int): Boolean {
		return false
	}
	
	fun setData(list: List<Pair<Bitmap?, Int?>>) {
		this.list = list
		notifyDataSetChanged()
	}
}