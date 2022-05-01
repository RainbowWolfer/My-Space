package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserProfileBinding
import com.rainbowwolfer.myspacedemo1.models.User

class UserProfileFragment(
	private val user: User
) : Fragment(R.layout.fragment_user_profile) {
	private val binding: FragmentUserProfileBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.userTextUsername.text = user.username
		binding.userTextDescription.text = user.profileDescription
	}
}