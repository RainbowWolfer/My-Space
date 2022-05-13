package com.rainbowwolfer.myspacedemo1.ui.fragments.main.imagesdisplay

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentZoomImageBinding
import com.rainbowwolfer.myspacedemo1.models.PostInfo.Companion.getImage
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication

class ZoomImageFragment : Fragment(R.layout.fragment_zoom_image) {
	private var position: Int = -1
	private var postID: String? = null
	
	private val binding: FragmentZoomImageBinding by viewBinding()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			postID = it.getString(ARG_POST_ID)
			position = it.getInt(ARG_IMAGE_INDEX)
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val bitmap = MySpaceApplication.instance.postImagesPool.getImage(postID!!, position)
		if (bitmap != null) {
			binding.zoomImageImageView.setImage(ImageSource.bitmap(bitmap))
		}
	}
	
	companion object {
		const val ARG_IMAGE_INDEX = "image_index"
		const val ARG_POST_ID = "post_id"
		
		@JvmStatic
		fun newInstance(postID: String, index: Int) = ZoomImageFragment().apply {
			arguments = Bundle().apply {
				putString(ARG_POST_ID, postID)
				putInt(ARG_IMAGE_INDEX, index)
			}
		}
	}
}