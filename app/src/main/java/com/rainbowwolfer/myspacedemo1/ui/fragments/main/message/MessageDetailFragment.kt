package com.rainbowwolfer.myspacedemo1.ui.fragments.main.message

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageDetailBinding
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageDetailRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.viewmodel.MessageFragmentViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessageDetailFragment : Fragment(R.layout.fragment_message_detail) {
	companion object {
		const val ARG_CONTACT = "message_contact"
	}
	
	private val binding: FragmentMessageDetailBinding by viewBinding()
	private val viewModel: MessageFragmentViewModel by viewModels(
		ownerProducer = { requireParentFragment() }
	)
	private val application = MySpaceApplication.instance
	private val adapter by lazy { MessageDetailRecyclerViewAdapter(requireContext()) }
	
	private lateinit var contact: MessageContact
	private var isLoading = false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		contact = arguments?.getParcelable(ARG_CONTACT)!!
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		(requireActivity() as AppCompatActivity).supportActionBar?.title = requireContext().getString(R.string.chat) + " " + contact.username
		
		binding.messageDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
			reverseLayout = true
		}
		binding.messageDetailRecyclerView.adapter = adapter
		
		viewModel.messages.observe(viewLifecycleOwner) {
			adapter.setData(it.filter { message ->
				message.senderID == contact.senderID.toString()
			})
		}
		binding.messageDetailRecyclerView.scrollToUpdate {
			loadMessages(false)
		}
		
		loadMessages(true)
	}
	
	private fun loadMessages(refresh: Boolean) {
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				if (isLoading) {
					return@launch
				}
				isLoading = true
				
				EasyFunctions.stackLoading(refresh, viewModel.messages, viewModel.offset) {
					RetrofitInstance.api.getMessages(
						email = application.getCurrentEmail(),
						password = application.getCurrentPassword(),
						offset = viewModel.offset.value ?: 0,
					)
				}
				
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is ResponseException) {
					ex.printResponseException()
				}
			} finally {
				try {
					isLoading = false
				} catch (ex: Exception) {
				
				}
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.message_detail_menu, menu)
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.item_info -> {
				Toast.makeText(requireContext(), contact.toString(), Toast.LENGTH_SHORT).show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}