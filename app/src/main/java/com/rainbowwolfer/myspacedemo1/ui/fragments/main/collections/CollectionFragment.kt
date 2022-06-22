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
		
		viewModel.collections.observe(viewLifecycleOwner) {
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
				
				EasyFunctions.stackLoading(refresh, viewModel.collections, viewModel.offset) {
					RetrofitInstance.api.getCollections(
						email = application.currentUser.value!!.email,
						password = application.currentUser.value!!.password,
						offset = viewModel.offset.value!!,
					)
				}
			} catch (ex: Exception) {
				success = false
				ex.printStackTrace()
				if (ex is ResponseException) {
					ex.printResponseException()
				}
			} finally {
				try {
					isLoading = false
					binding.collectionsSwipeLoader.isRefreshing = false
					if (refresh && success) {
						Toast.makeText(requireContext(), getString(R.string.refresh_successful), Toast.LENGTH_SHORT).show()
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
		return when (item.itemId) {
			R.id.item_refresh -> {
				lifecycleScope.launch(Dispatchers.Main) {
					delay(100)
					load(true)
				}
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}