package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.adapters.UserViewPagerAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.viewmodels.UserFragmentViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFragment : Fragment(R.layout.fragment_user) {
	companion object {
		const val ARG_ENABLE_MESSAGE = "enable_message"
		var instance: UserFragment? = null
	}
	
	private val binding: FragmentUserBinding by viewBinding()
	
	private val application = MySpaceApplication.instance
	private val viewModel: UserFragmentViewModel by viewModels()
	
	init {
		instance = this
	}
	
	var isSelf: Boolean = false
		private set
	
	var enableMessage = true
		private set
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		enableMessage = arguments?.getBoolean(ARG_ENABLE_MESSAGE, true) ?: true
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val userID: String
		if (arguments != null) {
			userID = UserFragmentArgs.fromBundle(requireArguments()).userId
			isSelf = false
		} else {
			userID = application.currentUser.value!!.id
			isSelf = true
		}
		
		if (TextUtils.isEmpty(userID)) {
			binding.userLayoutMain.visibility = View.GONE
			binding.userIncludeNotLoggedIn.root.visibility = View.VISIBLE
			return
		}
		
		val adapter = UserViewPagerAdapter(this, userID)
		binding.userViewPager2.adapter = adapter
		
		viewModel.postsAndFollowersCount.observe(viewLifecycleOwner) {
			TabLayoutMediator(binding.userTabLayout, binding.userViewPager2) { tab, position ->
				tab.text = when (position) {
					0 -> getString(R.string.profile)
					1 -> getString(R.string.posts) + if (it.first == -1) "" else " (${it.first})"
					2 -> getString(R.string.followers) + if (it.second == -1) "" else " (${it.second})"
					else -> getString(R.string.undefined)
				}
			}.attach()
		}
		
		lifecycleScope.launch(Dispatchers.IO) {
			try {
				val response = RetrofitInstance.api.getPostsAndFollowersCount(userID)
				val value = if (response.isSuccessful) {
					response.body() ?: listOf(-1, -1)
				} else {
					throw ResponseException(response.getHttpResponse())
				}
				if (value.size == 2) {
					withContext(Dispatchers.Main) {
						viewModel.postsAndFollowersCount.value = Pair(value[0], value[1])
					}
				} else {
					throw Exception("value size is ${value.size} which is not expected as 2")
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.user_menu, menu)
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.item_report -> {
				Toast.makeText(requireContext(), "Report", Toast.LENGTH_SHORT).show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}