package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.userprofile

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentUserFollowersBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.FollowersRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.viewmodels.UserFragmentViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserFollowersFragment : Fragment(R.layout.fragment_user_followers) {
	companion object {
		private const val ARGS_USER_ID = "user_id"
		
		
		fun newInstance(user_id: String) = UserFollowersFragment().apply {
			arguments = Bundle().apply {
				putString(ARGS_USER_ID, user_id)
			}
		}
	}
	
	private val binding: FragmentUserFollowersBinding by viewBinding()
	private val viewModel: UserFragmentViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	private val adapter by lazy { FollowersRecyclerViewAdapter(requireContext(), viewLifecycleOwner, userID) }
	
	private var isLoading = false
	private lateinit var userID: String
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			userID = it.getString(ARGS_USER_ID)!!
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.followersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.followersRecyclerView.adapter = adapter
		
		viewModel.followers.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		
		binding.followersRecyclerView.scrollToUpdate {
			load(false)
		}
		
		load(true)
	}
	
	private fun load(refresh: Boolean) {
		if (isLoading) {
			return
		}
		isLoading = true
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				EasyFunctions.stackLoading(refresh, viewModel.followers, viewModel.followersOffset) {
					RetrofitInstance.api.getUserFollowers(
						user_id = userID,
						email = application.getCurrentEmail(),
						password = application.getCurrentPassword(),
					)
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is ResponseException) {
					println(ex.response)
				}
			} finally {
				try {
					isLoading = false
					if (refresh) {
						binding.followersRecyclerView.scrollToPosition(0)
					}
				} catch (ex: Exception) {
				}
			}
		}
	}
}