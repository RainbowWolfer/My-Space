package com.rainbowwolfer.myspacedemo1.ui.fragments.main.drafts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentDraftsBinding

class DraftsFragment : Fragment(R.layout.fragment_drafts) {
	companion object {
		@JvmStatic
		fun newInstance() = DraftsFragment().apply {
			arguments = Bundle().apply {
				
			}
		}
	}
	
	private val binding: FragmentDraftsBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
	}
}