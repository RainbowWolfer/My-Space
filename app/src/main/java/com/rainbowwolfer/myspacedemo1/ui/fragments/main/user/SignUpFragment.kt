package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSignUpBinding

class SignUpFragment(
	private val loginActivity: LoginActivity,
) : Fragment(R.layout.fragment_sign_up) {
	private val binding: FragmentSignUpBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
	}
}