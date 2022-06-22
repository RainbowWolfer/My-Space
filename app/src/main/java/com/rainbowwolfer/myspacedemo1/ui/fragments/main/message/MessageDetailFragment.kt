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
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageDetailBinding
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.models.flag.FlagMessage
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageDetailRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.viewmodel.MessageFragmentViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
		
		application.roomRepository.getMessagesBySenderID(contact.senderID.toString()).asLiveData().observe(viewLifecycleOwner) {
			viewModel.messages.value = it
		}
		
		viewModel.messages.observe(viewLifecycleOwner) {
			println(it)
			adapter.setData(it)
		}
		binding.messageDetailRecyclerView.scrollToUpdate {
			loadMessages(false)
		}
		
		loadMessages(true)
	}
	
	private suspend fun saveLocal(messages: Array<Message>) {
		application.roomRepository.insertMessages(*messages)
	}
	
	private fun loadMessages(refresh: Boolean) {
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				if (isLoading) {
					return@launch
				}
				isLoading = true
				if (refresh) {
					viewModel.messages.value = emptyList()
					viewModel.offset.value = 0
				}
				
				EasyFunctions.stackLoading(refresh, viewModel.messages, viewModel.offset) {
					RetrofitInstance.api.getMessages(
						email = application.getCurrentEmail(),
						password = application.getCurrentPassword(),
						contactID = contact.senderID.toString(),
						offset = viewModel.offset.value ?: 0,
					)
				}
				
				withContext(Dispatchers.IO) {
					saveLocal(viewModel.messages.value!!.toTypedArray())
					RetrofitInstance.api.flagReceived(
						FlagMessage(
							email = application.getCurrentEmail(),
							password = application.getCurrentPassword(),
							senderID = contact.senderID.toString(),
							flagHasReceived = true,
						)
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