package com.rainbowwolfer.myspacedemo1.ui.fragments.main.message

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageDetailBinding
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.models.flag.FlagMessage
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.chat.ChatSocket
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageDetailRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.viewmodel.MessageFragmentViewModel
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.user.UserFragment
import com.rainbowwolfer.myspacedemo1.util.DateTimeUtils.getDateTime
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.defaultTransitionNavOption
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.scrollToUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime


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
		
		class WrapContentLinearLayoutManager : LinearLayoutManager(requireContext()) {
			override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
				try {
					binding.messageDetailRecyclerView.scrollToPosition(0)
					super.onLayoutChildren(recycler, state)
				} catch (ex: IndexOutOfBoundsException) {
					//java.lang.IndexOutOfBoundsException: Inconsistency detected
					ex.printStackTrace()
				}
			}
		}
		binding.messageDetailRecyclerView.layoutManager = WrapContentLinearLayoutManager().apply {
			reverseLayout = true
		}
		binding.messageDetailRecyclerView.adapter = adapter
		
		application.roomRepository.getMessagesBySenderID(contact.senderID.toString()).asLiveData().observe(viewLifecycleOwner) {
			viewModel.messages.value = it
		}
		
		viewModel.messages.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		binding.messageDetailRecyclerView.scrollToUpdate {
			loadMessages(false)
		}
		try {
			ChatSocket.read.observe(viewLifecycleOwner) {
				ChatSocket.handle(it) { message ->
					message.hasReceived = true
					//add to adapter
					adapter.addData(message)
					//add to room
					lifecycleScope.launch(Dispatchers.IO) {
						application.roomRepository.insertMessages(message)
					}
				}
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
		
		binding.messageButtonSend.setOnClickListener {
			//send message
			val content = binding.messageEditContent.text.toString().trim()
			if (content.isBlank()) {
				return@setOnClickListener
			}
			ChatSocket.console("/sign ${application.getCurrentID()}")
			ChatSocket.console("/msg ${contact.senderID} $content")
			
			val message = Message(
				id = 0,
				senderID = application.getCurrentID(),
				receiverID = contact.senderID.toString(),
				dateTime = DateTime.now().getDateTime(),
				textContent = content,
				hasReceived = false,
			)
			// add to adapter
			adapter.addData(message)
			binding.messageEditContent.setText("")
			
			// add to room
			lifecycleScope.launch(Dispatchers.IO) {
				application.roomRepository.insertMessages(message)
			}
			lifecycleScope.launch(Dispatchers.Main) {
				try {
					delay(100)
					binding.messageDetailRecyclerView.smoothScrollToPosition(0)
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
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
					adapter.clearData()
					viewModel.messages.value = emptyList()
					viewModel.offset.value = 0
				}
				
				EasyFunctions.stackLoading(refresh, viewModel.messages, viewModel.offset, 1) {
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
				findNavController().navigate(R.id.sub_nav_profile, Bundle().apply {
					putString("user_id", contact.senderID.toString())
					putBoolean(UserFragment.ARG_ENABLE_MESSAGE, false)
				}, defaultTransitionNavOption())
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}