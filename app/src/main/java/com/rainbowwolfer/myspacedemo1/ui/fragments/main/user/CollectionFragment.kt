package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentCollectionBinding

class CollectionFragment : Fragment(R.layout.fragment_collection) {
	private val binding: FragmentCollectionBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
	}
}