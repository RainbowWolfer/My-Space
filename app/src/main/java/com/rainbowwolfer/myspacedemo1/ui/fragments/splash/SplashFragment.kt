package com.rainbowwolfer.myspacedemo1.ui.fragments.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSplashBinding
import com.rainbowwolfer.myspacedemo1.models.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.repositories.UserPreferencesRepository.Companion.hasValue
import kotlinx.coroutines.*

class SplashFragment : Fragment(R.layout.fragment_splash) {
	private val binding: FragmentSplashBinding by viewBinding()
	private val application = MySpaceApplication.instance
	
	private var skip = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val data = application.userPreferencesRepository.getValue()
		data.asLiveData().observe(viewLifecycleOwner) {
			skip = it.skip
			if (it.hasValue()) {
				CoroutineScope(Dispatchers.IO).launch {
					try {
						val response = RetrofitInstance.api.tryLogin(it.email, it.password)
						val user = response.body()!!
						withContext(Dispatchers.Main) {
							application.currentUser.value = user
						}
					} catch (ex: Exception) {
						ex.printStackTrace()
					}
				}
			}
			CoroutineScope(Dispatchers.Main).launch {
				delay(1000)
				loadNext()
			}
		}
	}
	
	private fun loadNext() {
		if (skip) {
			jumpToMainActivity(this)
		} else {
			findNavController().navigate(R.id.action_splashFragment_to_welcomeViewPagerFragment)
		}
	}
	
	companion object {
		@JvmStatic
		fun jumpToMainActivity(fragment: Fragment) {
			CoroutineScope(Dispatchers.Main).launch {
				MySpaceApplication.instance.userPreferencesRepository.updateSkip(true)
			}
			fragment.startActivity(Intent(fragment.requireContext(), MainActivity::class.java))
			fragment.activity?.finish()
		}
	}
}