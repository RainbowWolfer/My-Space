package com.rainbowwolfer.myspacedemo1.ui.fragments.main.message

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentMessageBinding
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUser
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.chat.ChatSocket
import com.rainbowwolfer.myspacedemo1.services.recyclerview.adapters.MessageRecyclerViewAdapter
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.home.HomeFragment
import com.rainbowwolfer.myspacedemo1.ui.fragments.main.message.viewmodel.MessageFragmentViewModel
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MessageFragment : Fragment(R.layout.fragment_message) {
	private val binding: FragmentMessageBinding by viewBinding()
	private val viewModel: MessageFragmentViewModel by viewModels()
	private val adapter by lazy { MessageRecyclerViewAdapter(requireContext(), viewLifecycleOwner) }
	private val application = MySpaceApplication.instance
	
	override fun onResume() {
		super.onResume()
		ChatSocket.connect()
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}
	
	private var isLoading = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.messageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.messageRecyclerView.adapter = adapter
		
		try {
			ChatSocket.read.observe(viewLifecycleOwner) {
				ChatSocket.handle(it) { message ->
					//check contact exists
					message.hasReceived = false
					
					var contact: MessageContact? = null
					for (c in viewModel.contacts.value!!) {
						if (c.senderID.toString() == message.senderID) {
							contact = c
						}
					}
					if (contact != null) {
						contact.textContent = message.textContent
						contact.dateTime = message.dateTime
						contact.unreadCount += 1
						adapter.setData(listOf(contact))
						lifecycleScope.launch(Dispatchers.IO) {
							application.roomRepository.updateContact(contact)
						}
					} else {
						val new = MessageContact(
							senderID = message.senderID.toLong(),
							username = application.usersPool.findUser(message.senderID)?.username ?: getString(R.string.not_found),
							textContent = message.textContent,
							dateTime = message.dateTime,
							unreadCount = 1,
						)
						lifecycleScope.launch(Dispatchers.Main) {
							try {
								withContext(Dispatchers.IO) {
									val response = RetrofitInstance.api.getUser(message.senderID)
									if (response.isSuccessful) {
										new.username = response.body()?.username ?: getString(R.string.not_found)
									} else {
										throw ResponseException(response.getHttpResponse())
									}
									viewModel.contacts.value = viewModel.contacts.value!!.plus(new)
									application.roomRepository.insertContacts(new)
								}
							} catch (ex: Exception) {
								ex.printStackTrace()
							}
						}
					}
				}
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
		
		val all = application.roomRepository.allContacts
		all.asLiveData().observe(viewLifecycleOwner) {
			viewModel.contacts.value = it
		}
		
		viewModel.contacts.observe(viewLifecycleOwner) {
			adapter.setData(it)
		}
		
		binding.messageSwipeRefreshLayout.setOnRefreshListener {
			loadFromServer()
		}
		
		binding.messageFabAdd.setOnClickListener {
			HomeFragment.popupNotLoggedInHint(requireView())
		}
		
		loadFromServer()
	}
	
	private suspend fun saveLocal(contacts: Array<MessageContact>) {
		application.roomRepository.insertContacts(*contacts)
	}
	
	private fun loadFromServer() {
		lifecycleScope.launch(Dispatchers.Main) {
			try {
				if (isLoading) {
					return@launch
				}
				isLoading = true
				binding.messageSwipeRefreshLayout.isRefreshing = true
				val list = withContext(Dispatchers.IO) {
					val response = RetrofitInstance.api.getMessageContacts(
						email = application.getCurrentEmail(),
						password = application.getCurrentPassword(),
					)
					if (response.isSuccessful) {
						response.body() ?: emptyList()
					} else {
						throw ResponseException(response.getHttpResponse())
					}
				}
				viewModel.contacts.value = list
				withContext(Dispatchers.IO) {
					saveLocal(list.toTypedArray())
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is ResponseException) {
					ex.printResponseException()
				}
			} finally {
				try {
					isLoading = false
					binding.messageSwipeRefreshLayout.isRefreshing = false
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.messages_menu, menu)
		
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.item_selection -> {
				println(ChatSocket.test())
//				val contact = viewModel.contacts.value!!.firstOrNull()
//				if (contact != null) {
//					contact.unreadCount += 5
//					adapter.setData(viewModel.contacts.value!!)
//					println(viewModel.contacts.value!!)
//					adapter.notifyDataSetChanged()
//				}
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
	
}