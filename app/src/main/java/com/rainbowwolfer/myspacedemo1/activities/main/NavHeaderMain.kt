package com.rainbowwolfer.myspacedemo1.activities.main

import android.content.Context
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.rainbowwolfer.myspacedemo1.R

class NavHeaderMain(
	private val context: Context,
	private val viewGroup: ViewGroup,
) {
	val buttonLogin: MaterialButton = viewGroup.findViewById(R.id.header_button_login)
	
}