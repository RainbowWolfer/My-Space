package com.rainbowwolfer.myspacedemo1.ui.fragments.main.collections

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentCollectionBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.collections.adapters.recyclerview.CollectionsRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CollectionFragment : Fragment(R.layout.fragment_collection) {
	companion object {
		const val RELOAD_THREASHOLD = 3
	}
	
	private val binding: FragmentCollectionBinding by viewBinding()
	private val viewModel: CollectionsFragmentViewModel by viewModels()
	private val application = MySpaceApplication.instance
	private val adapter by lazy { CollectionsRecyclerViewAdapter(requireContext(), lifecycleScope) }
	
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.collectionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.collectionsRecyclerView.adapter = adapter
		
		load(true)
		
		binding.collectionsSwipeLoader.setOnRefreshListener {
			load(true)
		}
		
		binding.collectionsRecyclerView.scrollToUpdate {
			load(false)
		}
		
		viewModel.collecitons.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		
	}
	
	private fun load(refresh: Boolean) {
		if (isLoading || !application.hasLoggedIn()) {
			return
		}
		isLoading = true
		var success = true
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				binding.collectionsSwipeLoader.isRefreshing = true
				
				EasyFunctions.stackLoading(refresh, viewModel.collecitons, viewModel.offset) {
					RetrofitInstance.api.getCollections(
						email = application.currentUser.value!!.email,
						password = application.currentUser.value!!.password,
						offset = viewModel.offset.value!!,
					)
				}
//				var list: List<UserCollection> = if (refresh) emptyList() else viewModel.collecitons.value!!
//				var triedCount = 0
//				do {
//					val new = withContext(Dispatchers.IO) {
//						val response = RetrofitInstance.api.getCollections(
//							email = application.currentUser.value!!.email,
//							password = application.currentUser.value!!.password,
//							offset = viewModel.offset.value!!,
//						)
//						if (response.isSuccessful) {
//							response.body()!!
//						} else {
//							throw ResponseException(response.getHttpResponse())
//						}
//					}
//					var count = 0
//					if (new.isNotEmpty()) {
//						for (item in new) {
//							if (list.any { it.id == item.id }) {
//								continue
//							}
//							list = list.plus(item)
//							count++
//						}
//						viewModel.offset.value = viewModel.offset.value!!.plus(count)
//					}
//					viewModel.collecitons.value = list
//				} while (new.isNotEmpty() && count <= RELOAD_THREASHOLD && triedCount++ <= 5)
			} catch (ex: Exception) {
				success = false
				ex.printStackTrace()
				if (ex is ResponseException) {
					println(ex.response)
				}
			} finally {
				try {
					isLoading = false
					binding.collectionsSwipeLoader.isRefreshing = false
					if (refresh && success) {
						Toast.makeText(requireContext(), "Refresh Successsful", Toast.LENGTH_SHORT).show()
					}
				} catch (ex: Exception) {
				}
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.collections_menu, menu)
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.item_refresh -> {
				lifecycleScope.launch(Dispatchers.Main) {
					delay(100)
					load(true)
				}
			}
		}
		return true
	}
}