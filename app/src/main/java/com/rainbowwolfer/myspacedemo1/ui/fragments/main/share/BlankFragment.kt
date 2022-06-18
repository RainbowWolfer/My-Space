package com.rainbowwolfer.myspacedemo1.ui.fragments.main.share

import android.os.Bundle
import androidx.fragment.app.Fragment

class BlankFragment : Fragment() {
	private var param1: String? = null
	private var param2: String? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			param1 = it.getString(ARG_PARAM1)
			param2 = it.getString(ARG_PARAM2)
		}
	}
	
	companion object {
		private const val ARG_PARAM1 = "param1"
		private const val ARG_PARAM2 = "param2"
		
		
		fun newInstance(param1: String, param2: String) =
			BlankFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_PARAM1, param1)
					putString(ARG_PARAM2, param2)
				}
			}
	}
}